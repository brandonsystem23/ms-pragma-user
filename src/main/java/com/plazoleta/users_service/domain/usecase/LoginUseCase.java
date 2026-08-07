package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.api.AuthServicePort;
import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import com.plazoleta.users_service.domain.usecase.auth.AuthResult;
import com.plazoleta.users_service.domain.usecase.auth.LoginCommand;
import com.plazoleta.users_service.domain.spi.JwtProviderPort;
import com.plazoleta.users_service.domain.spi.PasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.UsuarioPersistencePort;
import reactor.core.publisher.Mono;

public class LoginUseCase implements AuthServicePort {

    private final UsuarioPersistencePort usuarioPersistencePort;

    private final PasswordEncoderPort passwordEncoderPort;

    private final JwtProviderPort jwtProviderPort;

    public LoginUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            JwtProviderPort jwtProviderPort) {

        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtProviderPort = jwtProviderPort;

    }

    @Override
    public Mono<AuthResult> login(LoginCommand command) {

        return usuarioPersistencePort.findByDocumento(command.documentoIdentidad())
                .switchIfEmpty(
                        Mono.error(new UserNotFoundException())
                )
                .filter(usuario ->
                        passwordEncoderPort.matches(
                                command.password(),
                                usuario.getPassword())
                )
                .switchIfEmpty(
                        Mono.error(new InvalidCredentialsException())
                )
                .map(usuario ->
                        AuthResult.builder()
                                .token(jwtProviderPort.generateToken(usuario))
                                .tokenType("Bearer")
                                .userId(usuario.getId())
                                .role(usuario.getRol().getNombre())
                                .build()

                );

    }

}
