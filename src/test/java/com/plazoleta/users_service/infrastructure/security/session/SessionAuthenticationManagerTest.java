package com.plazoleta.users_service.infrastructure.security.session;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionAuthenticationManagerTest {

    @Mock
    private AuthSessionPort authSessionPort;

    @InjectMocks
    private SessionAuthenticationManager authenticationManager;

    @Test
    void shouldAuthenticateSuccessfully() {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .role("ADMINISTRADOR")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(authSessionPort.findByToken(anyString())).thenReturn(Mono.just(session));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "token-123");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .assertNext(result -> {
                    Assertions.assertTrue(result.isAuthenticated());
                    Assertions.assertEquals(1L, result.getPrincipal());

                    Assertions.assertTrue(
                            result.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        when(authSessionPort.findByToken(anyString())).thenReturn(Mono.empty());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "invalid-token");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .expectError(BadCredentialsException.class)
                .verify();
    }
}