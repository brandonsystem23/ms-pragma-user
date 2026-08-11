package com.plazoleta.users_service.infrastructure.output.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RedisAuthSessionAdapterTest {

    private ReactiveStringRedisTemplate redisTemplate;
    private ReactiveValueOperations<String, String> valueOperations;
    private ObjectMapper objectMapper;
    private RedisAuthSessionAdapter adapter;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(ReactiveStringRedisTemplate.class);
        valueOperations = mock(ReactiveValueOperations.class);
        objectMapper = new ObjectMapper();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        adapter = new RedisAuthSessionAdapter(redisTemplate, objectMapper, Duration.ofMillis(3600000L));
    }

    @Test
    void shouldCreateSessionSuccessfully() {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMIN")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(valueOperations.set(anyString(), anyString(), any()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(adapter.createSession(session))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void shouldFindSessionByTokenSuccessfully() throws Exception {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMIN")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        String json = objectMapper.writeValueAsString(session);

        when(valueOperations.get("auth:token:test-token"))
                .thenReturn(Mono.just(json));

        StepVerifier.create(adapter.findByToken("test-token"))
                .assertNext(found -> {
                    assert 1L == found.userId();
                    assert "ADMIN".equals(found.role());
                    assert "123456".equals(found.numberDocument());
                })
                .verifyComplete();
    }

    @Test
    void shouldDeleteTokenSuccessfully() {
        when(redisTemplate.delete("auth:token:test-token"))
                .thenReturn(Mono.just(1L));

        StepVerifier.create(adapter.deleteByToken("test-token"))
                .verifyComplete();
    }
}
