package com.plazoleta.users_service.infrastructure.output.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisAuthSessionAdapterTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    private RedisAuthSessionAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RedisAuthSessionAdapter(
                redisTemplate,
                objectMapper,
                Duration.ofMinutes(10)
        );
    }


    @Test
    void shouldCreateSessionSuccessfully() throws JsonProcessingException {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMINISTRADOR")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{}");

        when(valueOperations.set(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(adapter.createSession(session))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void shouldFindSessionByTokenSuccessfully() throws Exception {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMINISTRADOR")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(any()))
                .thenReturn(Mono.just("{}"));

        when(objectMapper.readValue(
                anyString(),
                eq(AuthSession.class)
        )).thenReturn(session);

        StepVerifier.create(adapter.findByToken("test-token"))
                .assertNext(found -> {
                    Assertions.assertEquals(1L, found.userId());
                    Assertions.assertEquals("ADMINISTRADOR", found.role());
                    Assertions.assertEquals("123456", found.numberDocument());
                })
                .verifyComplete();
    }

    @Test
    void shouldDeleteTokenSuccessfully() {
        when(redisTemplate.delete(anyString()))
                .thenReturn(Mono.just(1L));

        StepVerifier.create(adapter.deleteByToken("test-token"))
                .verifyComplete();

        verify(redisTemplate).delete("auth:token:test-token");
    }

    @Test
    void shouldReturnErrorWhenSessionIsNotSavedInRedis() throws JsonProcessingException {

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMINISTRADOR")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{}");

        when(valueOperations.set(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(false));

        StepVerifier.create(adapter.createSession(session))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException
                                && error.getMessage().equals("No se pudo almacenar la sesión en Redis"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenSessionSerializationFails() throws JsonProcessingException {

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMINISTRADOR")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        JsonProcessingException exception =
                new JsonProcessingException("Error de serialización") {};

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(exception);

        StepVerifier.create(adapter.createSession(session))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException
                                && error.getMessage().equals(
                                "Error serializando la sesión")
                                && error.getCause() == exception)
                .verify();

    }

    @Test
    void shouldReturnErrorWhenSessionDeserializationFails()
            throws JsonProcessingException {

        JsonProcessingException exception =
                new JsonProcessingException("Error de deserialización") {};

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(anyString()))
                .thenReturn(Mono.just("{}"));

        when(objectMapper.readValue(
                anyString(),
                eq(AuthSession.class)))
                .thenThrow(exception);

        StepVerifier.create(adapter.findByToken("test-token"))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException
                                && error.getMessage().equals(
                                "Error deserializando la sesión")
                                && error.getCause() == exception)
                .verify();
    }
}
