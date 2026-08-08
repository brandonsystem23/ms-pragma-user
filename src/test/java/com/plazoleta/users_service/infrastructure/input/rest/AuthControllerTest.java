package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.application.service.AuthApplicationService;
import com.plazoleta.users_service.application.service.LogoutApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private AuthApplicationService authApplicationService;
    private LogoutApplicationService logoutApplicationService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authApplicationService = mock(AuthApplicationService.class);
        logoutApplicationService = mock(LogoutApplicationService.class);
        authController = new AuthController(authApplicationService, logoutApplicationService);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("admin@test.com", "123456");

        LoginResponse response = LoginResponse.builder()
                .token("token-123")
                .tokenType("Bearer")
                .userId(1L)
                .role("ADMIN")
                .build();

        when(authApplicationService.login(request)).thenReturn(Mono.just(response));

        StepVerifier.create(authController.login(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldLogoutSuccessfully() {
        when(logoutApplicationService.logout("token-123")).thenReturn(Mono.empty());

        StepVerifier.create(authController.logout("Bearer token-123"))
                .verifyComplete();
    }

    @Test
    void shouldFailLogoutWhenAuthorizationHeaderIsInvalid() {
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> authController.logout("Basic token-123")
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Authorization header inválido",
                exception.getMessage()
        );
    }

}