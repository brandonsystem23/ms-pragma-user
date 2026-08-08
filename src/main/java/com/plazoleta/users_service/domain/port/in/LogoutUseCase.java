package com.plazoleta.users_service.domain.port.in;

import reactor.core.publisher.Mono;

public interface LogoutUseCase {

    Mono<Void> logout(String token);
}
