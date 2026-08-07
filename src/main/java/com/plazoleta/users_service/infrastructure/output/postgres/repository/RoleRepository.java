package com.plazoleta.users_service.infrastructure.output.postgres.repository;

import com.plazoleta.users_service.infrastructure.output.postgres.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<RoleEntity, Long> {

    Mono<RoleEntity> findByName(String name);

}
