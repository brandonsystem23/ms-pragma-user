package com.plazoleta.users_service.domain.service.validation;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;

public class DomainUserValidator {

    public void validateForRegister(RegisterUserCommand command) {
        validateCommonFields(command);
        validateRoleSpecificRules(command);
    }

    private void validateCommonFields(RegisterUserCommand command) {
        if (ValidationUtils.isBlank(command.firstName())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.FIRST_NAME_REQUIRED);
        }

        if (ValidationUtils.isBlank(command.lastName())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.LAST_NAME_REQUIRED);
        }

        if (ValidationUtils.isBlank(command.numberDocument())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.DOCUMENT_REQUIRED);
        }

        if (!DocumentValidator.isValid(command.numberDocument())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.DOCUMENT_NUMERIC);
        }

        if (ValidationUtils.isBlank(command.phone())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.PHONE_REQUIRED);
        }

        if (command.phone().length() > 13) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.PHONE_MAX_LENGTH);
        }

        if (!PhoneValidator.hasValidFormat(command.phone())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.PHONE_INVALID);
        }

        if (ValidationUtils.isBlank(command.email())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.EMAIL_REQUIRED);
        }

        if (EmailValidator.isInvalid(command.email())) {
            throw new DomainException(
                    DomainErrorCode.VALIDATION_ERROR,
                    DomainErrorMessages.EMAIL_INVALID
            );
        }

        if (ValidationUtils.isBlank(command.password())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.PASS_REQUIRED);
        }
    }

    private void validateRoleSpecificRules(RegisterUserCommand command) {
        if (RoleNames.OWNER.equals(command.roleName())) {
            if (command.birthDate() == null) {
                throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.BIRTH_DATE_REQUIRED);
            }

            if (!AdultValidation.isAdult(command.birthDate())) {
                throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.USER_MUST_BE_ADULT);
            }
        }
    }
}
