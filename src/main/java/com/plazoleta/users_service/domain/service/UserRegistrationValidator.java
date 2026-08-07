package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.domain.port.out.UsuarioPersistencePort;
import reactor.core.publisher.Mono;

public class UserRegistrationValidator {

    private final UsuarioPersistencePort usuarioPersistencePort;

    public UserRegistrationValidator(UsuarioPersistencePort usuarioPersistencePort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
    }

    public Mono<Rol> validate(String documentoIdentidad, String correo, String roleName) {
        return validateDocument(documentoIdentidad)
                .then(validateEmail(correo))
                .then(usuarioPersistencePort.findRoleByNombre(roleName))
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleName)));
    }

    private Mono<Void> validateDocument(String documento) {
        return usuarioPersistencePort.existsByDocumento(documento)
                .flatMap(exists -> exists
                        ? Mono.error(new DuplicateDocumentException())
                        : Mono.empty());
    }

    private Mono<Void> validateEmail(String correo) {
        return usuarioPersistencePort.existsByCorreo(correo)
                .flatMap(exists -> exists
                        ? Mono.error(new DuplicateEmailException())
                        : Mono.empty());
    }
}