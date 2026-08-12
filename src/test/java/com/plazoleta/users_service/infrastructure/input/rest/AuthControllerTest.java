package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.application.service.AuthApplicationService;
import com.plazoleta.users_service.application.service.LogoutApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthApplicationService authApplicationService;

    @Mock
    private LogoutApplicationService logoutApplicationService;

    @InjectMocks
    private AuthController authController;


    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("admin@test.com", "123456");

        LoginResponse response = LoginResponse.builder()
                .token("token-123")
                .tokenType("Bearer")
                .userId(1L)
                .role("ADMIN")
                .build();

        when(authApplicationService.login(any())).thenReturn(Mono.just(response));

        StepVerifier.create(authController.login(request))
                .assertNext(login -> {
                    Assertions.assertEquals("Bearer", login.tokenType());
                    Assertions.assertEquals("token-123", login.token());
                })
                .verifyComplete();
    }

    @Test
    void shouldLogoutSuccessfully() {
        when(logoutApplicationService.logout(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authController.logout("Bearer token-123"))
                .verifyComplete();
    }

    @Test
    void shouldFailLogoutWhenAuthorizationHeaderIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authController.logout("Basic token-123")
        );
    }

}