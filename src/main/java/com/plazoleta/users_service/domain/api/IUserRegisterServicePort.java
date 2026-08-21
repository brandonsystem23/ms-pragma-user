package com.plazoleta.users_service.domain.api;

import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import reactor.core.publisher.Mono;

public interface IUserRegisterServicePort {

    Mono<User> register(RegisterUserCommand command, Long ownerId);
}
