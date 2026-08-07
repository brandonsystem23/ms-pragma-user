package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthApplicationServiceTest {

    private LoginUseCase loginUseCase;
    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        loginUseCase = mock(LoginUseCase.class);
        authApplicationService = new AuthApplicationService(loginUseCase);
    }

    @Test
    void shouldReturnLoginResponseSuccessfully() {
        LoginRequest request = new LoginRequest("test@test.com", "123456");

        AuthResult authResult = AuthResult.builder()
                .token("token-123")
                .tokenType("Bearer")
                .userId(1L)
                .role("ADMIN")
                .build();

        when(loginUseCase.login(new com.plazoleta.users_service.domain.model.auth.LoginCommand("test@test.com", "123456")))
                .thenReturn(Mono.just(authResult));

        StepVerifier.create(authApplicationService.login(request))
                .assertNext(response -> {
                    assert "token-123".equals(response.token());
                    assert "Bearer".equals(response.tokenType());
                    assert 1L == response.userId();
                    assert "ADMIN".equals(response.role());
                })
                .verifyComplete();
    }
}
