package com.plazoleta.users_service.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public boolean isValid(
            LocalDate birthDate,
            ConstraintValidatorContext context) {

        if (birthDate == null) {
            return true;
        }

        return Period.between(
                birthDate,
                LocalDate.now(ZoneId.of("America/Lima"))
        ).getYears() >= 18;
    }
}
