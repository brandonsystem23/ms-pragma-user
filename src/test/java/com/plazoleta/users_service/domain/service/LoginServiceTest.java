package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private UsuarioPersistencePort usuarioPersistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private AuthSessionPort authSessionPort;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        usuarioPersistencePort = mock(UsuarioPersistencePort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        authSessionPort = mock(AuthSessionPort.class);
        loginService = new LoginService(usuarioPersistencePort, passwordEncoderPort, authSessionPort);
    }

    @Test
    void shouldLoginSuccessfully() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .nombre("Ana")
                .apellido("Lopez")
                .documentoIdentidad("123456")
                .telefono("+573001112233")
                .correo("ana@test.com")
                .password("encoded-password")
                .activo(true)
                .rol(Rol.builder().id(1L).nombre("ADMIN").descripcion("Administrador").build())
                .build();

        when(usuarioPersistencePort.findByCorreo("ana@test.com")).thenReturn(Mono.just(usuario));
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
        when(usuarioPersistencePort.findByCorreo("notfound@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(loginService.login(new LoginCommand("notfound@test.com", "123456")))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .correo("ana@test.com")
                .password("encoded-password")
                .rol(Rol.builder().id(1L).nombre("ADMIN").descripcion("Administrador").build())
                .build();

        when(usuarioPersistencePort.findByCorreo("ana@test.com")).thenReturn(Mono.just(usuario));
        when(passwordEncoderPort.matches("wrong-password", "encoded-password")).thenReturn(false);

        StepVerifier.create(loginService.login(new LoginCommand("ana@test.com", "wrong-password")))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }
}
