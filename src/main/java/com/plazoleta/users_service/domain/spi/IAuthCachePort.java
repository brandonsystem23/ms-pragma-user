package com.plazoleta.users_service.domain.spi;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import reactor.core.publisher.Mono;

public interface IAuthCachePort {

    Mono<String> createSession(AuthSession authSession);

    Mono<AuthSession> findByToken(String token);

    Mono<Void> deleteByToken(String token);

}
