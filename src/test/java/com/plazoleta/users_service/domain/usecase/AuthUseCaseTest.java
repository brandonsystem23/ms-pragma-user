package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.domain.validation.user.DomainLoginValidator;
import com.plazoleta.users_service.domain.validation.user.LoginValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IAuthCachePort iAuthCachePort;

    @Mock
    private LoginValidator loginValidator;

    @Mock
    private DomainLoginValidator domainLoginValidator;

    @InjectMocks
    private AuthUseCase authUseCase;

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
        doNothing().when(domainLoginValidator).validate(any());
        when(loginValidator.validate(any(), anyString())).thenReturn(Mono.just(user));

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

        doNothing().when(domainLoginValidator).validate(any());
        when(loginValidator.validate(any(), anyString())).thenThrow(new DomainException(
                DomainErrorCode.USER_NOT_FOUND,
                DomainErrorMessages.USER_NOT_FOUND
        ));

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
        doThrow(new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.EMAIL_REQUIRED))
                .when(domainLoginValidator).validate(any());

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

        doNothing().when(domainLoginValidator).validate(any());
        when(loginValidator.validate(any(), anyString())).thenThrow(new DomainException(
                DomainErrorCode.INVALID_CREDENTIALS,
                DomainErrorMessages.INVALID_CREDENTIALS
        ));

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
