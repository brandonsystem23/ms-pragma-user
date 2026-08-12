package com.plazoleta.users_service.infrastructure.security.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BearerTokenExtractorTest {

    @Test
    void shouldExtractTokenSuccessfully() {
        String token = BearerTokenExtractor.extract("Bearer abc123");
        assertEquals("abc123", token);
    }

    @Test
    void shouldThrowExceptionWhenHeaderIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> BearerTokenExtractor.extract(null)
        );

    }

    @Test
    void shouldThrowExceptionWhenHeaderIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BearerTokenExtractor.extract("Basic abc123")
        );
    }
}
