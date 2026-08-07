package com.plazoleta.users_service.infrastructure.security.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordEncoderAdapterTest {

    private PasswordEncoder passwordEncoder;
    private PasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        adapter = new PasswordEncoderAdapter(passwordEncoder);
    }

    @Test
    void shouldEncodePassword() {
        when(passwordEncoder.encode("123456")).thenReturn("encoded");

        String result = adapter.encode("123456");

        assertEquals("encoded", result);
    }

    @Test
    void shouldMatchPasswords() {
        when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);

        boolean result = adapter.matches("123456", "encoded");

        assertTrue(result);
    }
}
