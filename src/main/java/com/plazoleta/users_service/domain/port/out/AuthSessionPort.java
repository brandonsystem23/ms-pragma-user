package com.plazoleta.users_service.domain.port.out;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import reactor.core.publisher.Mono;

public interface AuthSessionPort {

    Mono<String> createSession(AuthSession authSession);

    Mono<AuthSession> findByToken(String token);

}
