package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.domain.service.validation.DomainLoginValidator;
import com.plazoleta.users_service.domain.util.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthSessionPort authSessionPort;
    private final DomainLoginValidator domainLoginValidator;

    @Override
    public Mono<AuthResult> login(LoginCommand loginCommand) {
        return Mono.defer(() -> {

            String normalizedEmail = EmailNormalizer.normalize(loginCommand.email());

            domainLoginValidator.validate(loginCommand);

            return userPersistencePort.findByEmail(normalizedEmail)
                    .switchIfEmpty(Mono.error(new DomainException(
                            DomainErrorCode.USER_NOT_FOUND,
                            DomainErrorMessages.USER_NOT_FOUND
                    )))
                    .filter(user -> passwordEncoderPort.matches(loginCommand.password(), user.getPassword()))
                    .switchIfEmpty(Mono.error(new DomainException(
                            DomainErrorCode.INVALID_CREDENTIALS,
                            DomainErrorMessages.INVALID_CREDENTIALS
                    )))
                    .flatMap(user -> authSessionPort.createSession(
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
}
