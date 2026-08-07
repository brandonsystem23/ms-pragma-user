package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class LogoutApplicationServiceTest {

    private AuthSessionPort authSessionPort;
    private LogoutApplicationService logoutApplicationService;

    @BeforeEach
    void setUp() {
        authSessionPort = mock(AuthSessionPort.class);
        logoutApplicationService = new LogoutApplicationService(authSessionPort);
    }

    @Test
    void shouldLogoutSuccessfully() {
        when(authSessionPort.deleteByToken("token-123")).thenReturn(Mono.empty());

        StepVerifier.create(logoutApplicationService.logout("token-123"))
                .verifyComplete();

        verify(authSessionPort).deleteByToken("token-123");
    }
}
