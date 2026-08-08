package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import reactor.core.publisher.Mono;

public class UserRegistrationValidator {

    private final UserPersistencePort userPersistencePort;

    public UserRegistrationValidator(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    public Mono<Role> validate(String numberDocument, String email, String roleName) {
        return validateDocument(numberDocument)
                .then(validateEmail(email))
                .then(userPersistencePort.findRoleByName(roleName))
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleName)));
    }

    private Mono<Void> validateDocument(String numberDocument) {
        return userPersistencePort.existsByNumberDocument(numberDocument)
                .flatMap(exists -> exists
                        ? Mono.error(new DuplicateDocumentException())
                        : Mono.empty());
    }

    private Mono<Void> validateEmail(String email) {
        return userPersistencePort.existsByEmail(email)
                .flatMap(exists -> exists
                        ? Mono.error(new DuplicateEmailException())
                        : Mono.empty());
    }
}