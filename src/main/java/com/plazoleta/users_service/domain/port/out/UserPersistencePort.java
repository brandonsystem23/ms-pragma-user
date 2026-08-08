package com.plazoleta.users_service.domain.port.out;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserPersistencePort {

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByNumberDocument(String numberDocument);

    Mono<Role> findRoleByName(String name);

    Mono<User> save(User user);
}
