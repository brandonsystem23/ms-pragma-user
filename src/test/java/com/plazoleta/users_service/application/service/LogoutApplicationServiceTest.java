package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutApplicationServiceTest {

    @Mock
    private LogoutUseCase logoutUseCase;

    @InjectMocks
    private LogoutApplicationService logoutApplicationService;

    @Test
    void shouldLogoutSuccessfully() {
        String token = "test-token";

        when(logoutUseCase.logout(token)).thenReturn(Mono.empty());

        StepVerifier.create(logoutApplicationService.logout(token))
                .verifyComplete();
    }
}
