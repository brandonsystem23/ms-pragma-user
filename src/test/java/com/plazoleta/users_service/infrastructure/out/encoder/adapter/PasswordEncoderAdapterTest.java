package com.plazoleta.users_service.infrastructure.out.encoder.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderAdapterTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderAdapter adapter;


    @Test
    void shouldEncodePassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        String result = adapter.encode("123456");

        assertEquals("encoded", result);
    }

    @Test
    void shouldMatchPasswords() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean result = adapter.matches("123456", "encoded");

        assertTrue(result);
    }
}
