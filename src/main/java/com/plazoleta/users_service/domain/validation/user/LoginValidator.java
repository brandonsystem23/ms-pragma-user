package com.plazoleta.users_service.domain.validation.user;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginValidator {

    private final IUserPersistencePort iUserPersistencePort;
    private final IPasswordEncoderPort iPasswordEncoderPort;

    public Mono<User> validate(LoginCommand loginCommand, String email) {
        return validateEmail(email)
                .filter(user -> iPasswordEncoderPort.matches(loginCommand.password(), user.getPassword()))
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.INVALID_CREDENTIALS,
                        DomainErrorMessages.INVALID_CREDENTIALS
                )));
    }

    private Mono<User> validateEmail(String email) {
        return iUserPersistencePort.findByEmail(email)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.USER_NOT_FOUND,
                        DomainErrorMessages.USER_NOT_FOUND
                )));
    }






}
