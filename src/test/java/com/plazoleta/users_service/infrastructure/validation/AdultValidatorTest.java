package com.plazoleta.users_service.infrastructure.validation;

import com.plazoleta.users_service.application.validation.AdultValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AdultValidatorTest {

    private final AdultValidator validator = new AdultValidator();

    @Test
    void shouldReturnTrueWhenAgeIs18OrMore() {
        boolean result = validator.isValid(LocalDate.now().minusYears(20), null);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenAgeIsLessThan18() {
        boolean result = validator.isValid(LocalDate.now().minusYears(17), null);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenDateIsNull() {
        boolean result = validator.isValid(null, null);
        assertTrue(result);
    }
}
