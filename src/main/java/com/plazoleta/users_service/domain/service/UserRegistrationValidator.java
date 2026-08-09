package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserRegistrationValidator {

    private final UserPersistencePort userPersistencePort;

    public Mono<Role> validate(String numberDocument, String email, String roleName) {
        return validateDocument(numberDocument)
                .then(validateEmail(email))
                .then(userPersistencePort.findRoleByName(roleName))
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleName)));
    }

    private Mono<Void> validateDocument(String numberDocument) {
        return userPersistencePort.existsByNumberDocument(numberDocument)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new DuplicateDocumentException())
                        : Mono.empty());
    }

    private Mono<Void> validateEmail(String email) {
        return userPersistencePort.existsByEmail(email)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new DuplicateEmailException())
                        : Mono.empty());
    }
}
