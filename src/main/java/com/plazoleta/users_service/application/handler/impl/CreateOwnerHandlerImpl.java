package com.plazoleta.users_service.application.handler.impl;

import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.handler.CreateOwnerHandler;
import com.plazoleta.users_service.domain.api.CreateOwnerUseCase;
import com.plazoleta.users_service.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateOwnerHandlerImpl implements CreateOwnerHandler {

    private final CreateOwnerUseCase createOwnerUseCase;

    @Override
    public Mono<Usuario> createOwner(CreateOwnerRequest request) {

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .documentoIdentidad(request.documentoIdentidad())
                .telefono(request.celular())
                .fechaNacimiento(request.fechaNacimiento())
                .correo(request.correo())
                .password(request.password())
                .activo(true)
                .build();

        return createOwnerUseCase.createOwner(usuario);
    }
}
