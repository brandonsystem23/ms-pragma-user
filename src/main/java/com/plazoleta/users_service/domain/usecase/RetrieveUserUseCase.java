package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.api.IUserRetrieveServicePort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RetrieveUserUseCase implements IUserRetrieveServicePort {

    private final IUserPersistencePort iUserPersistencePort;

    @Override
    public Mono<User> find(Long id) {
        return iUserPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.USER_NOT_FOUND,
                        DomainErrorMessages.USER_NOT_FOUND
                )));
    }
}
