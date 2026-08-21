package com.plazoleta.users_service.domain.validation;

import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.validation.DomainUserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DomainUserValidatorTest {

    private DomainUserValidator domainUserValidator;

    @BeforeEach
    void setUp() {
        domainUserValidator = new DomainUserValidator();
    }

    @Test
    void shouldValidateOwnerSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertDoesNotThrow(() -> domainUserValidator.validateForRegister(command));
    }

    @Test
    void shouldValidateEmployeeSuccessfullyWithoutBirthDate() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Ana",
                "Lopez",
                "987654321",
                "+573001112233",
                null,
                "ana@test.com",
                "123456",
                RoleNames.EMPLOYEE
        );

        Assertions.assertDoesNotThrow(() -> domainUserValidator.validateForRegister(command));
    }

    @Test
    void shouldValidateClientSuccessfullyWithoutBirthDate() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Carlos",
                "Gomez",
                "1122334455",
                "+573009998877",
                null,
                "carlos@test.com",
                "123456",
                RoleNames.CLIENT
        );

        Assertions.assertDoesNotThrow(() -> domainUserValidator.validateForRegister(command));
    }

    @Test
    void shouldFailWhenFirstNameIsNull() {
        RegisterUserCommand command = new RegisterUserCommand(
                null,
                "Perez",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenLastNameIsBlank() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "   ",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenDocumentIsNull() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                null,
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenDocumentIsNotNumeric() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "ABC123",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );
    }

    @Test
    void shouldFailWhenPhoneIsNull() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                null,
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenPhoneExceedsMaxLength() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+57300123456789",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenEmailIsNull() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(20),
                null,
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenEmailFormatIsInvalid() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "invalid-email",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenPasswordIsNull() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                null,
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenOwnerBirthDateIsNull() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+573001234567",
                null,
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldFailWhenOwnerIsUnderage() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "+573001234567",
                LocalDate.now().minusYears(17),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );

    }

    @Test
    void shouldAllowEmployeeWithoutBirthDate() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Laura",
                "Martinez",
                "555666777",
                "+573005556677",
                null,
                "laura@test.com",
                "123456",
                RoleNames.EMPLOYEE
        );

        Assertions.assertDoesNotThrow(() -> domainUserValidator.validateForRegister(command));
    }

    @Test
    void shouldAllowClientWithoutBirthDate() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Pedro",
                "Ramirez",
                "999888777",
                "+573009887766",
                null,
                "pedro@test.com",
                "123456",
                RoleNames.CLIENT
        );

        Assertions.assertDoesNotThrow(() -> domainUserValidator.validateForRegister(command));
    }

    @Test
    void shouldFailWhenPhoneFormatIsInvalid() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Perez",
                "123456789",
                "ABC123",
                LocalDate.now().minusYears(20),
                "juan@test.com",
                "123456",
                RoleNames.OWNER
        );

        Assertions.assertThrows(
                DomainException.class,
                () -> domainUserValidator.validateForRegister(command)
        );
    }

}
