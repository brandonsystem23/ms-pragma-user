package com.plazoleta.users_service.infrastructure.out.redis.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.infrastructure.out.redis.dto.AuthSessionRedisValue;
import com.plazoleta.users_service.infrastructure.out.redis.mapper.RedisRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionRedisAdapter implements IAuthCachePort {

    private static final String PREFIX = "auth:token:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration expiration;
    private final RedisRequestMapper redisRequestMapper;

    @Override
    public Mono<String> createSession(AuthSession authSession) {
        String token = UUID.randomUUID().toString();
        String key = buildKey(token);

        AuthSessionRedisValue redisValue = redisRequestMapper.toInsert(authSession);
        return serialize(redisValue)
                .flatMap(json -> redisTemplate.opsForValue().set(key, json, expiration))
                .flatMap(saved -> Boolean.TRUE.equals(saved)
                        ? Mono.just(token)
                        : Mono.error(new IllegalStateException("No se pudo almacenar la sesión en Redis")));
    }

    @Override
    public Mono<AuthSession> findByToken(String token) {
        return redisTemplate.opsForValue()
                .get(buildKey(token))
                .flatMap(this::deserialize)
                .map(redisRequestMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByToken(String token) {
        return redisTemplate.delete(buildKey(token)).then();
    }

    private String buildKey(String token) {
        return PREFIX + token;
    }

    private Mono<String> serialize(AuthSessionRedisValue authSessionRedisValue) {
        try {
            return Mono.just(objectMapper.writeValueAsString(authSessionRedisValue));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error serializando la sesión", e));
        }
    }

    private Mono<AuthSessionRedisValue> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, AuthSessionRedisValue.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error deserializando la sesión", e));
        }
    }
}
