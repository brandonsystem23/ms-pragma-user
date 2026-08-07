package com.plazoleta.users_service.infrastructure.output.postgres.repository;

import com.plazoleta.users_service.infrastructure.output.postgres.entity.UsuarioEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioRepository extends ReactiveCrudRepository<UsuarioEntity, Long> {

    Mono<UsuarioEntity> findByCorreo(String correo);

    Mono<Boolean> existsByCorreo(String correo);

    Mono<Boolean> existsByDocumentoIdentidad(String documento);
}
