package com.plazoleta.users_service.domain.api;

import com.plazoleta.users_service.domain.usecase.auth.AuthResult;
import com.plazoleta.users_service.domain.usecase.auth.LoginCommand;
import reactor.core.publisher.Mono;

public interface AuthServicePort {

    Mono<AuthResult> login(LoginCommand command);
}
