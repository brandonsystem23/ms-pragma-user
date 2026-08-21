package com.plazoleta.users_service.domain.service.validation;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;

public class DomainLoginValidator {

    public void validate(LoginCommand command) {
        if (ValidationUtils.isBlank(command.email())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.EMAIL_REQUIRED);
        }

        if (EmailValidator.isInvalid(command.email())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.EMAIL_INVALID);
        }

        if (ValidationUtils.isBlank(command.password())) {
            throw new DomainException(DomainErrorCode.VALIDATION_ERROR, DomainErrorMessages.PASS_REQUIRED);
        }
    }
}
