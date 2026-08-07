package com.plazoleta.users_service.domain.api;

import com.plazoleta.users_service.domain.model.Usuario;
import reactor.core.publisher.Mono;

public interface CreateOwnerUseCase {

    Mono<Usuario> createOwner(Usuario usuario);
}
