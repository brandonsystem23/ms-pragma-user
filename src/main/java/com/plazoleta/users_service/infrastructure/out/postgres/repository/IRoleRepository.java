package com.plazoleta.users_service.infrastructure.out.postgres.repository;

import com.plazoleta.users_service.infrastructure.out.postgres.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IRoleRepository extends ReactiveCrudRepository<RoleEntity, Long> {

    Mono<RoleEntity> findByName(String name);

}
