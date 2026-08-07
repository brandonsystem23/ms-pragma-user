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
import reactor.core.publisher.Mono;

public class LoginService implements LoginUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthSessionPort authSessionPort;

    public LoginService(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            AuthSessionPort authSessionPort
    ) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.authSessionPort = authSessionPort;
    }

    @Override
    public Mono<AuthResult> login(LoginCommand command) {
        String normalizedEmail = normalizeEmail(command.email());

        return userPersistencePort.findByEmail(normalizedEmail)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .filter(usuario -> passwordEncoderPort.matches(command.password(), usuario.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(usuario -> authSessionPort.createSession(
                                AuthSession.builder()
                                        .userId(usuario.getId())
                                        .role(usuario.getRole().getName())
                                        .numberDocument(usuario.getNumberDocument())
                                        .phone(usuario.getPhone())
                                        .email(usuario.getEmail())
                                        .build()
                        )
                        .map(token -> AuthResult.builder()
                                .token(token)
                                .tokenType("Bearer")
                                .userId(usuario.getId())
                                .role(usuario.getRole().getName())
                                .build()));
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
