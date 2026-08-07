package com.plazoleta.users_service.domain.port.in;

import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import reactor.core.publisher.Mono;

public interface LoginUseCase {

    Mono<AuthResult> login(LoginCommand command);
}