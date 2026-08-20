package com.plazoleta.users_service.domain.service.validation;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
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
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.ROLE_NOT_FOUND,
                        "El rol " + roleName + " no existe"
                )))
                .map(foundRole -> foundRole);
    }

    private Mono<Void> validateDocument(String numberDocument) {
        return userPersistencePort.existsByNumberDocument(numberDocument)
                .flatMap(documentAlreadyExists -> Boolean.TRUE.equals(documentAlreadyExists)
                        ? Mono.error(new DomainException(
                        DomainErrorCode.DUPLICATE_DOCUMENT,
                        DomainErrorMessages.DUPLICATE_DOCUMENT
                ))
                        : Mono.empty());
    }

    private Mono<Void> validateEmail(String email) {
        return userPersistencePort.existsByEmail(email)
                .flatMap(emailAlreadyExists -> Boolean.TRUE.equals(emailAlreadyExists)
                        ? Mono.error(new DomainException(
                        DomainErrorCode.DUPLICATE_EMAIL,
                        DomainErrorMessages.DUPLICATE_EMAIL
                ))
                        : Mono.empty());
    }
}
