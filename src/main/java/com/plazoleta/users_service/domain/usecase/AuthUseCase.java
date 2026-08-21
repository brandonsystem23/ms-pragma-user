package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.api.IAuthServicePort;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.domain.service.validation.DomainLoginValidator;
import com.plazoleta.users_service.domain.service.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort iUserPersistencePort;
    private final IPasswordEncoderPort iPasswordEncoderPort;
    private final IAuthCachePort iAuthCachePort;
    private final DomainLoginValidator domainLoginValidator;

    @Override
    public Mono<AuthResult> login(LoginCommand loginCommand) {
        return Mono.defer(() -> {

            String normalizedEmail = EmailNormalizer.normalize(loginCommand.email());

            domainLoginValidator.validate(loginCommand);

            return iUserPersistencePort.findByEmail(normalizedEmail)
                    .switchIfEmpty(Mono.error(new DomainException(
                            DomainErrorCode.USER_NOT_FOUND,
                            DomainErrorMessages.USER_NOT_FOUND
                    )))
                    .filter(user -> iPasswordEncoderPort.matches(loginCommand.password(), user.getPassword()))
                    .switchIfEmpty(Mono.error(new DomainException(
                            DomainErrorCode.INVALID_CREDENTIALS,
                            DomainErrorMessages.INVALID_CREDENTIALS
                    )))
                    .flatMap(user -> iAuthCachePort.createSession(
                                    AuthSession.builder()
                                            .userId(user.getId())
                                            .fullName(user.getFirstName().concat(" ").concat(user.getLastName()))
                                            .role(user.getRole().getName())
                                            .numberDocument(user.getNumberDocument())
                                            .phone(user.getPhone())
                                            .email(user.getEmail())
                                            .build()
                            )
                            .map(token -> AuthResult.builder()
                                    .token(token)
                                    .tokenType("Bearer")
                                    .userId(user.getId())
                                    .role(user.getRole().getName())
                                    .build()));
        });
    }

    @Override
    public Mono<Void> logout(String token) {
        return iAuthCachePort.deleteByToken(token);
    }
}
