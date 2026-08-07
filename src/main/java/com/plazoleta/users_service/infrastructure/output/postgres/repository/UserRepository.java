package com.plazoleta.users_service.infrastructure.output.postgres.repository;

import com.plazoleta.users_service.infrastructure.output.postgres.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByNumberDocument(String numberDocument);
}
