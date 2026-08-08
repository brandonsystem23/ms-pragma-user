package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.domain.port.in.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LogoutApplicationService {

    private final LogoutUseCase logoutUseCase;

    public Mono<Void> logout(String token) {
        return logoutUseCase.logout(token);
    }
}

