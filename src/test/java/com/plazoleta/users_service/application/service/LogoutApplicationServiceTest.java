package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LogoutApplicationServiceTest {

    private LogoutUseCase logoutUseCase;
    private LogoutApplicationService logoutApplicationService;

    @BeforeEach
    void setUp() {
        logoutUseCase = Mockito.mock(LogoutUseCase.class);
        logoutApplicationService = new LogoutApplicationService(logoutUseCase);
    }

    @Test
    void shouldLogoutSuccessfully() {
        String token = "test-token";

        Mockito.when(logoutUseCase.logout(token)).thenReturn(Mono.empty());

        StepVerifier.create(logoutApplicationService.logout(token))
                .verifyComplete();

        Mockito.verify(logoutUseCase).logout(token);
    }
}
