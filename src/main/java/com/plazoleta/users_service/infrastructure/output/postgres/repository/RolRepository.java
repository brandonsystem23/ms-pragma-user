package com.plazoleta.users_service.infrastructure.output.postgres.repository;

import com.plazoleta.users_service.infrastructure.output.postgres.entity.RolEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolRepository extends ReactiveCrudRepository<RolEntity, Long> {

    Mono<RolEntity> findByNombre(String nombre);

}
