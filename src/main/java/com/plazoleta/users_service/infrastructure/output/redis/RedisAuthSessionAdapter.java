package com.plazoleta.users_service.infrastructure.output.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Component
public class RedisAuthSessionAdapter implements AuthSessionPort {

    private static final String PREFIX = "auth:token:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration expiration;

    public RedisAuthSessionAdapter(
            ReactiveStringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            @Value("${auth.token.expiration}") Long expirationMillis
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.expiration = Duration.ofMillis(expirationMillis);
    }

    @Override
    public Mono<String> createSession(AuthSession authSession) {
        String token = UUID.randomUUID().toString();

        return serialize(authSession)
                .flatMap(json -> redisTemplate.opsForValue()
                        .set(PREFIX + token, json, expiration)
                        .flatMap(saved -> saved
                                ? Mono.just(token)
                                : Mono.error(new IllegalStateException("No se pudo almacenar la sesión en Redis"))));
    }

    @Override
    public Mono<AuthSession> findByToken(String token) {
        return redisTemplate.opsForValue()
                .get(PREFIX + token)
                .flatMap(this::deserialize);
    }

    @Override
    public Mono<Void> deleteByToken(String token) {
        return redisTemplate.delete(PREFIX + token).then();
    }

    private Mono<String> serialize(AuthSession authSession) {
        try {
            return Mono.just(objectMapper.writeValueAsString(authSession));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error serializando la sesión"));
        }
    }

    private Mono<AuthSession> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, AuthSession.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error deserializando la sesión"));
        }
    }
}
