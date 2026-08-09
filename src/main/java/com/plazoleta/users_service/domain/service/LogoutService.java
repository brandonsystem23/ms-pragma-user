package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final AuthSessionPort authSessionPort;

    @Override
    public Mono<Void> logout(String token) {
        return authSessionPort.deleteByToken(token);
    }
}

