package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.application.service.AuthApplicationService;
import com.plazoleta.users_service.application.service.LogoutApplicationService;
import com.plazoleta.users_service.infrastructure.security.session.BearerTokenExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de sesión")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final LogoutApplicationService logoutApplicationService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token de sesión")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authApplicationService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cerrar sesión", description = "Invalida el token actual eliminando la sesión en Redis")
    public Mono<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return Mono.fromCallable(() -> BearerTokenExtractor.extract(authorizationHeader))
                .flatMap(logoutApplicationService::logout);
    }
}
