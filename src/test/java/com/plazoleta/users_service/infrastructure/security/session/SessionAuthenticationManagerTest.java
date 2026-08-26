package com.plazoleta.users_service.infrastructure.security.session;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.spi.IJwtProviderPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionAuthenticationManagerTest {

    @Mock
    private IJwtProviderPort iJwtProviderPort;

    @InjectMocks
    private SessionAuthenticationManager authenticationManager;

    @Test
    void shouldAuthenticateSuccessfully() {
        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Admin User")
                .role("ADMINISTRADOR")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(iJwtProviderPort.validateAndGetSession(anyString()))
                .thenReturn(session);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "jwt-token-123");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .assertNext(result -> {
                    Assertions.assertTrue(result.isAuthenticated());
                    Assertions.assertEquals(1L, result.getPrincipal());

                    Assertions.assertTrue(
                            result.getAuthorities().stream()
                                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMINISTRADOR"))
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        when(iJwtProviderPort.validateAndGetSession(anyString()))
                .thenThrow(new RuntimeException("Token inválido"));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "invalid-token");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .expectError(BadCredentialsException.class)
                .verify();
    }
}
