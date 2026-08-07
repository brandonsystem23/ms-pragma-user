package com.plazoleta.users_service.infrastructure.security.session;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionAuthenticationManagerTest {

    private AuthSessionPort authSessionPort;
    private SessionAuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        authSessionPort = mock(AuthSessionPort.class);
        authenticationManager = new SessionAuthenticationManager(authSessionPort);
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMIN")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(authSessionPort.findByToken("token-123")).thenReturn(Mono.just(session));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "token-123");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .assertNext(result -> {
                    assert result.isAuthenticated();
                    assert result.getPrincipal().equals(1L);
                    assert result.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        when(authSessionPort.findByToken("invalid-token")).thenReturn(Mono.empty());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "invalid-token");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .expectError(BadCredentialsException.class)
                .verify();
    }
}