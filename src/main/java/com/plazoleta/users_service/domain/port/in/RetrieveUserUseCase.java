package com.plazoleta.users_service.domain.port.in;

import com.plazoleta.users_service.domain.model.User;
import reactor.core.publisher.Mono;

public interface RetrieveUserUseCase {

    Mono<User> find(Long id);
}
