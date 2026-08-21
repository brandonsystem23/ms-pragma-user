package com.plazoleta.users_service.application.handler.impl;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.api.IAuthServicePort;
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
class AuthHandlerTest {
    @Mock
    private IAuthServicePort iAuthServicePort;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private AuthHandler authHandler;

    @Test
    void shouldReturnLoginResponseSuccessfully() {
        LoginRequest request = new LoginRequest("test@test.com", "123456");

        AuthResult authResult = AuthResult.builder()
                .token("token-123")
                .tokenType("Bearer")
                .userId(1L)
                .role("ADMINISTRADOR")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .token("token-123")
                .tokenType("Bearer")
                .userId(1L)
                .role("ADMINISTRADOR")
                .build();

        when(iAuthServicePort.login(any()))
                .thenReturn(Mono.just(authResult));

        when(userDtoMapper.toResponse(any(AuthResult.class)))
                .thenReturn(loginResponse);

        StepVerifier.create(authHandler.login(request))
                .assertNext(response -> {
                    Assertions.assertEquals("token-123", response.token());
                    Assertions.assertEquals("Bearer", response.tokenType());
                    Assertions.assertEquals(1L, response.userId());
                    Assertions.assertEquals("ADMINISTRADOR", response.role());
                })
                .verifyComplete();
    }


    @Test
    void shouldLogoutSuccessfully() {
        String token = "test-token";

        when(iAuthServicePort.logout(token)).thenReturn(Mono.empty());

        StepVerifier.create(authHandler.logout(token))
                .verifyComplete();
    }
}
