package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import reactor.core.publisher.Mono;

public class LogoutService implements LogoutUseCase {

    private final AuthSessionPort authSessionPort;

    public LogoutService(AuthSessionPort authSessionPort) {
        this.authSessionPort = authSessionPort;
    }

    @Override
    public Mono<Void> logout(String token) {
        return authSessionPort.deleteByToken(token);
    }
}

