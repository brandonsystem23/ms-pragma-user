package com.plazoleta.users_service.application.handler;

import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.domain.model.Usuario;
import reactor.core.publisher.Mono;

public interface CreateOwnerHandler {

    Mono<Usuario> createOwner(CreateOwnerRequest request);
}
