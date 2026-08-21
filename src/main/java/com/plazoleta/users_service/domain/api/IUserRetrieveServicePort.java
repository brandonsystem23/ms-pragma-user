package com.plazoleta.users_service.domain.api;

import com.plazoleta.users_service.domain.model.User;
import reactor.core.publisher.Mono;

public interface IUserRetrieveServicePort {

    Mono<User> find(Long id);
}
