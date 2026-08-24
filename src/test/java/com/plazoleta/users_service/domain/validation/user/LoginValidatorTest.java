package com.plazoleta.users_service.domain.validation.user;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginValidatorTest {

    @Mock
    private IUserPersistencePort iUserPersistencePort;

    @Mock
    private IPasswordEncoderPort iPasswordEncoderPort;

    @InjectMocks
    private LoginValidator validator;

    @Test
    void shouldValidateLoginSuccessfully() {

        LoginCommand loginCommand = new LoginCommand("test@test.com", "123456");

        User user = User.builder()
                .id(1L)
                .numberDocument("123456")
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        when(iUserPersistencePort.findByEmail(anyString()))
                .thenReturn(Mono.just(user));

        when(iPasswordEncoderPort.matches(anyString(), anyString())).thenReturn(true);

        StepVerifier.create(validator.validate(loginCommand, "test@test.com"))
                .assertNext(response -> {
                    Assertions.assertEquals(1L, response.getId());
                    Assertions.assertEquals("test@test.com", response.getEmail());
                    Assertions.assertEquals("encodedPassword", response.getPassword());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {

        LoginCommand loginCommand = new LoginCommand(
                "test@test.com",
                "123456"
        );

        when(iUserPersistencePort.findByEmail(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate(loginCommand, "test@test.com"))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;

                    Assertions.assertEquals(DomainErrorCode.USER_NOT_FOUND, exception.getCode());

                    Assertions.assertEquals(DomainErrorMessages.USER_NOT_FOUND, exception.getMessage());

                })
                .verify();

    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {

        LoginCommand loginCommand = new LoginCommand(
                "test@test.com",
                "wrongPassword"
        );

        User user = User.builder()
                .id(1L)
                .numberDocument("123456")
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        when(iUserPersistencePort.findByEmail(anyString()))
                .thenReturn(Mono.just(user));

        when(iPasswordEncoderPort.matches(anyString(), anyString()))
                .thenReturn(false);

        StepVerifier.create(validator.validate(loginCommand, "test@test.com"))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;

                    Assertions.assertEquals(DomainErrorCode.INVALID_CREDENTIALS, exception.getCode());

                    Assertions.assertEquals(DomainErrorMessages.INVALID_CREDENTIALS, exception.getMessage());
                })
                .verify();
    }
}