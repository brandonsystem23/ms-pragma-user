package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private UserPersistencePort usuarioPersistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private AuthSessionPort authSessionPort;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        usuarioPersistencePort = mock(UserPersistencePort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        authSessionPort = mock(AuthSessionPort.class);
        loginService = new LoginService(usuarioPersistencePort, passwordEncoderPort, authSessionPort);
    }

    @Test
    void shouldLoginSuccessfully() {
        User usuario = User.builder()
                .id(10L)
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .role(Role.builder().id(1L).name("ADMIN").description("Administrador").build())
                .build();

        when(usuarioPersistencePort.findByEmail("ana@test.com")).thenReturn(Mono.just(usuario));
        when(passwordEncoderPort.matches("123456", "encoded-password")).thenReturn(true);
        when(authSessionPort.createSession(any(AuthSession.class))).thenReturn(Mono.just("token-123"));

        StepVerifier.create(loginService.login(new LoginCommand("  ANA@test.com ", "123456")))
                .assertNext(result -> {
                    assert "token-123".equals(result.token());
                    assert "Bearer".equals(result.tokenType());
                    assert 10L == result.userId();
                    assert "ADMIN".equals(result.role());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(usuarioPersistencePort.findByEmail("notfound@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(loginService.login(new LoginCommand("notfound@test.com", "123456")))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {
        User usuario = User.builder()
                .id(10L)
                .email("ana@test.com")
                .password("encoded-password")
                .role(Role.builder().id(1L).name("ADMIN").description("Administrador").build())
                .build();

        when(usuarioPersistencePort.findByEmail("ana@test.com")).thenReturn(Mono.just(usuario));
        when(passwordEncoderPort.matches("wrong-password", "encoded-password")).thenReturn(false);

        StepVerifier.create(loginService.login(new LoginCommand("ana@test.com", "wrong-password")))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }
}
