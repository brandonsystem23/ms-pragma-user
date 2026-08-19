package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.port.in.RetrieveUserCase;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RetrieveUserService implements RetrieveUserCase {

    private final UserPersistencePort userPersistencePort;

    @Override
    public Mono<User> find(Long id) {
        return userPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.USER_NOT_FOUND,
                        DomainErrorMessages.USER_NOT_FOUND
                )));
    }
}
