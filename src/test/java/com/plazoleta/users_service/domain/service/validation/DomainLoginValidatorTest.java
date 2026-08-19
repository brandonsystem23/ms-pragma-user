package com.plazoleta.users_service.domain.service.validation;

import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DomainLoginValidatorTest {

    private DomainLoginValidator domainLoginValidator;

    @BeforeEach
    void setUp() {
        domainLoginValidator = new DomainLoginValidator();
    }


    @Test
    void shouldFailWhenEmailIsNull() {
        LoginCommand command = new LoginCommand(null, "123456");
        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        LoginCommand command = new LoginCommand("", "123456");

        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );
    }

    @Test
    void shouldFailWhenEmailHasOnlySpaces() {
        LoginCommand command = new LoginCommand("   ", "123456");

        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );

    }


    @Test
    void shouldFailWhenPasswordIsNull() {
        LoginCommand command = new LoginCommand("user@test.com", null);

        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );

    }

    @Test
    void shouldFailWhenPasswordIsEmpty() {
        LoginCommand command = new LoginCommand("user@test.com", "");

        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );

    }

    @Test
    void shouldFailWhenPasswordHasOnlySpaces() {
        LoginCommand command = new LoginCommand("user@test.com", "   ");

        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );
    }

    @Test
    void shouldFailWhenEmailFormatIsInvalid() {
        LoginCommand command = new LoginCommand("correo-invalido", "123456");

        Assertions.assertThrows(
                DomainException.class,
                () -> domainLoginValidator.validate(command)
        );
    }
}
