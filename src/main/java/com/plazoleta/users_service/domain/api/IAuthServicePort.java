package com.plazoleta.users_service.domain.api;

import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import reactor.core.publisher.Mono;

public interface IAuthServicePort {

    Mono<AuthResult> login(LoginCommand command);

    Mono<Void> logout(String token);
}