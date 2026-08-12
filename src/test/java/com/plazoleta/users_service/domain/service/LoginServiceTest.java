package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private AuthSessionPort authSessionPort;

    @InjectMocks
    private LoginService loginService;

    @Test
    void shouldLoginSuccessfully() {
        User user = User.builder()
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

        when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(true);
        when(authSessionPort.createSession(any())).thenReturn(Mono.just("token-123"));

        StepVerifier.create(loginService.login(new LoginCommand("  ANA@test.com ", "123456")))
                .assertNext(result -> {
                    Assertions.assertEquals("token-123", result.token());
                    Assertions.assertEquals("Bearer", result.tokenType());
                    Assertions.assertEquals(10L, result.userId());
                    Assertions.assertEquals("ADMIN", result.role());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(loginService.login(new LoginCommand("notfound@test.com", "123456")))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        when(userPersistencePort.findByEmail(any())).thenReturn(Mono.empty());

        StepVerifier.create(loginService.login(new LoginCommand(null, "123456")))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {
        User user = User.builder()
                .id(10L)
                .email("ana@test.com")
                .password("encoded-password")
                .role(Role.builder().id(1L).name("ADMIN").description("Administrador").build())
                .build();

        when(userPersistencePort.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(loginService.login(new LoginCommand("ana@test.com", "wrong-password")))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }
}
