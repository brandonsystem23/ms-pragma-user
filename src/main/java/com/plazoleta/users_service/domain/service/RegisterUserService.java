package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UsuarioPersistencePort;
import reactor.core.publisher.Mono;

public class RegisterUserService implements RegisterUserUseCase {

    private final UsuarioPersistencePort usuarioPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRegistrationValidator userRegistrationValidator;

    public RegisterUserService(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userRegistrationValidator = new UserRegistrationValidator(usuarioPersistencePort);
    }

    @Override
    public Mono<Usuario> register(RegisterUserCommand command) {
        String normalizedEmail = normalizeEmail(command.correo());

        return userRegistrationValidator.validate(
                        command.documentoIdentidad(),
                        normalizedEmail,
                        command.roleName()
                )
                .flatMap(rol -> {
                    Usuario usuario = Usuario.builder()
                            .nombre(command.nombre())
                            .apellido(command.apellido())
                            .documentoIdentidad(command.documentoIdentidad())
                            .telefono(command.telefono())
                            .fechaNacimiento(command.fechaNacimiento())
                            .correo(normalizedEmail)
                            .password(passwordEncoderPort.encode(command.password()))
                            .activo(true)
                            .rol(rol)
                            .build();

                    return usuarioPersistencePort.save(usuario);
                });
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
