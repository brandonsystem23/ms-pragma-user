package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {
    @Mock
    private AuthSessionPort authSessionPort;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void logout() {

        when(authSessionPort.deleteByToken(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(logoutService.logout("Bearer token"))
                .verifyComplete();
    }
}