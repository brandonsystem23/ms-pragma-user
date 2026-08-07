package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LogoutApplicationService {

    private final AuthSessionPort authSessionPort;

    public Mono<Void> logout(String token) {
        return authSessionPort.deleteByToken(token);
    }
}
