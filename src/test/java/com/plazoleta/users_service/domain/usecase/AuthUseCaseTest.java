package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.domain.validation.DomainLoginValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUserPersistencePort iUserPersistencePort;

    @Mock
    private IPasswordEncoderPort iPasswordEncoderPort;

    @Mock
    private IAuthCachePort iAuthCachePort;

    private AuthUseCase authUseCase;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCase(
                iUserPersistencePort,
                iPasswordEncoderPort,
                iAuthCachePort,
                new DomainLoginValidator()
        );
    }

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
                .role(Role.builder().id(1L).name("ADMINISTRADOR").description("Administrador").build())
                .build();

        when(iUserPersistencePort.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(iPasswordEncoderPort.matches(anyString(), anyString())).thenReturn(true);
        when(iAuthCachePort.createSession(any())).thenReturn(Mono.just("token-123"));

        StepVerifier.create(authUseCase.login(new LoginCommand("  ANA@test.com ", "123456")))
                .assertNext(result -> {
                    Assertions.assertEquals("token-123", result.token());
                    Assertions.assertEquals("Bearer", result.tokenType());
                    Assertions.assertEquals(10L, result.userId());
                    Assertions.assertEquals("ADMINISTRADOR", result.role());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(iUserPersistencePort.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.login(new LoginCommand("notfound@test.com", "123456")))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.USER_NOT_FOUND, exception.getCode());
                    Assertions.assertEquals(DomainErrorMessages.USER_NOT_FOUND, exception.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        StepVerifier.create(authUseCase.login(new LoginCommand(null, "123456")))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
                    Assertions.assertEquals(DomainErrorMessages.EMAIL_REQUIRED, exception.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {
        User user = User.builder()
                .id(10L)
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .role(Role.builder().id(1L).name("ADMINISTRADOR").description("Administrador").build())
                .build();

        when(iUserPersistencePort.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(iPasswordEncoderPort.matches(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(authUseCase.login(new LoginCommand("ana@test.com", "wrong-password")))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.INVALID_CREDENTIALS, exception.getCode());
                    Assertions.assertEquals(DomainErrorMessages.INVALID_CREDENTIALS, exception.getMessage());
                })
                .verify();


    }

    @Test
    void logout() {

        when(iAuthCachePort.deleteByToken(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.logout("Bearer token"))
                .verifyComplete();
    }

}
