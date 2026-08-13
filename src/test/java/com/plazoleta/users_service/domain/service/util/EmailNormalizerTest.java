package com.plazoleta.users_service.domain.service.util;

import com.plazoleta.users_service.domain.util.EmailNormalizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailNormalizerTest {

    @Test
    void shouldReturnNullWhenEmailIsNull() {
        assertNull(EmailNormalizer.normalize(null));
    }

    @Test
    void shouldNormalizeEmailToLowerCase() {
        String result = EmailNormalizer.normalize("USER@MAIL.COM");

        assertEquals("user@mail.com", result);
    }

    @Test
    void shouldTrimEmailBeforeNormalizing() {
        String result = EmailNormalizer.normalize("  user@mail.com  ");

        assertEquals("user@mail.com", result);
    }

    @Test
    void shouldTrimAndConvertToLowerCase() {
        String result = EmailNormalizer.normalize("  USER@MAIL.COM  ");

        assertEquals("user@mail.com", result);
    }

    @Test
    void shouldReturnEmptyStringWhenEmailContainsOnlySpaces() {
        String result = EmailNormalizer.normalize("   ");

        assertEquals("", result);
    }
}
