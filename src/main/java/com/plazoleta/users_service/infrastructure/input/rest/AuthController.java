package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.login(
                        new LoginCommand(
                                request.correo(),
                                request.password()
                        )
                )
                .map(authResult -> LoginResponse.builder()
                        .token(authResult.token())
                        .tokenType(authResult.tokenType())
                        .userId(authResult.userId())
                        .role(authResult.role())
                        .build());
    }
}
