package com.plazoleta.users_service.domain.port.out;

import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.domain.model.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioPersistencePort {

    Mono<Usuario> findByCorreo(String correo);

    Mono<Boolean> existsByCorreo(String correo);

    Mono<Boolean> existsByDocumento(String documento);

    Mono<Rol> findRoleByNombre(String nombre);

    Mono<Usuario> save(Usuario usuario);
}
