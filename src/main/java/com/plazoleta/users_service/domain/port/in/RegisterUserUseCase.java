package com.plazoleta.users_service.domain.port.in;

import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {

    Mono<User> register(RegisterUserCommand command);
}
