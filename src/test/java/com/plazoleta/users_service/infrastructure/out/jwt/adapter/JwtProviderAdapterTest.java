package com.plazoleta.users_service.infrastructure.out.jwt.adapter;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class JwtProviderAdapterTest {

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
        String secret = "my-super-secret-key-my-super-secret-key-123456";
        Duration expiration = Duration.ofMinutes(60);

        JwtProviderAdapter adapter = new JwtProviderAdapter(secret, expiration);
        adapter.init();

        AuthSession session = AuthSession.builder()
                .userId(7L)
                .fullName("Sofia Gomez")
                .role("EMPLEADO")
                .numberDocument("987654320")
                .phone("+573004445566")
                .email("sofia.gomez@plazoleta.com")
                .build();

        String token = adapter.generateToken(session);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());

        AuthSession decoded = adapter.validateAndGetSession(token);

        Assertions.assertEquals(7L, decoded.userId());
        Assertions.assertEquals("Sofia Gomez", decoded.fullName());
        Assertions.assertEquals("EMPLEADO", decoded.role());
        Assertions.assertEquals("987654320", decoded.numberDocument());
        Assertions.assertEquals("+573004445566", decoded.phone());
        Assertions.assertEquals("sofia.gomez@plazoleta.com", decoded.email());
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        String secret = "my-super-secret-key-my-super-secret-key-123456";
        Duration expiration = Duration.ofMinutes(60);

        JwtProviderAdapter adapter = new JwtProviderAdapter(secret, expiration);
        adapter.init();

        Assertions.assertThrows(Exception.class, () ->
                adapter.validateAndGetSession("token-invalido")
        );
    }
}
