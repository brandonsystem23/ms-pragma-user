package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {
    @Mock
    private LoginUseCase loginUseCase;

    @InjectMocks
    private AuthApplicationService authApplicationService;

    @Test
    void shouldReturnLoginResponseSuccessfully() {
        LoginRequest request = new LoginRequest("test@test.com", "123456");

        AuthResult authResult = AuthResult.builder()
                .token("token-123")
                .tokenType("Bearer")
                .userId(1L)
                .role("ADMIN")
                .build();

        when(loginUseCase.login(any()))
                .thenReturn(Mono.just(authResult));

        StepVerifier.create(authApplicationService.login(request))
                .assertNext(response -> {
                    Assertions.assertEquals("token-123", response.token());
                    Assertions.assertEquals("Bearer", response.tokenType());
                    Assertions.assertEquals(1L, response.userId());
                    Assertions.assertEquals("ADMIN", response.role());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnLoginResponseSuccessfullyTpeTokenNull() {
        LoginRequest request = new LoginRequest("test@test.com", "123456");

        AuthResult authResult = AuthResult.builder()
                .token("token-123")
                .tokenType(null)
                .userId(1L)
                .role("ADMIN")
                .build();

        when(loginUseCase.login(any()))
                .thenReturn(Mono.just(authResult));

        StepVerifier.create(authApplicationService.login(request))
                .assertNext(response -> {
                    Assertions.assertEquals("token-123", response.token());
                    Assertions.assertEquals("Bearer", response.tokenType());
                    Assertions.assertEquals(1L, response.userId());
                    Assertions.assertEquals("ADMIN", response.role());
                })
                .verifyComplete();
    }
}
