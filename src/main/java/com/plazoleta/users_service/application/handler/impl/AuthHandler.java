package com.plazoleta.users_service.application.handler.impl;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.application.handler.IAuthHandler;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.api.IAuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {

    private final IAuthServicePort iAuthServicePort;
    private final UserDtoMapper userDtoMapper;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return iAuthServicePort.login(new LoginCommand(request.email(), request.password()))
                .map(userDtoMapper::toResponse);
    }

    @Override
    public Mono<Void> logout(String token) {
        return iAuthServicePort.logout(token);
    }
}
