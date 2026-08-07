package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private static final String TOKEN_TYPE = "Bearer";

    private final LoginUseCase loginUseCase;

    public Mono<LoginResponse> login(LoginRequest request) {
        return loginUseCase.login(
                        new LoginCommand(
                                request.email(),
                                request.password()
                        )
                )
                .map(authResult -> LoginResponse.builder()
                        .token(authResult.token())
                        .tokenType(authResult.tokenType() != null ? authResult.tokenType() : TOKEN_TYPE)
                        .userId(authResult.userId())
                        .role(authResult.role())
                        .build());
    }
}
