package com.plazoleta.users_service.domain.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public final class AdultValidation {

    private AdultValidation() {
    }

    public static boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }

        return Period.between(
                birthDate,
                LocalDate.now(ZoneId.of("America/Lima"))
        ).getYears() >= 18;
    }
}
