package com.plazoleta.users_service.application.handler.impl;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.application.handler.LoginHandler;
import com.plazoleta.users_service.application.mapper.LoginMapper;
import com.plazoleta.users_service.domain.api.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class LoginHandlerImpl implements LoginHandler {

    private final AuthServicePort authServicePort;

    private final LoginMapper loginMapper;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {

        return authServicePort.login(loginMapper.toCommand(request))
                .map(loginMapper::toResponse);

    }

}
