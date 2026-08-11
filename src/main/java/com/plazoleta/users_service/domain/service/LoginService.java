package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.model.auth.LoginCommand;
import com.plazoleta.users_service.domain.port.in.LoginUseCase;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.domain.util.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthSessionPort authSessionPort;

    @Override
    public Mono<AuthResult> login(LoginCommand command) {
        String normalizedEmail = EmailNormalizer.normalize(command.email());

        return userPersistencePort.findByEmail(normalizedEmail)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .filter(user -> passwordEncoderPort.matches(command.password(), user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
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
    }
}
