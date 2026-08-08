package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import reactor.core.publisher.Mono;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRegistrationValidator userRegistrationValidator;

    public RegisterUserService(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userRegistrationValidator = new UserRegistrationValidator(userPersistencePort);
    }

    @Override
    public Mono<User> register(RegisterUserCommand command) {
        String normalizedEmail = normalizeEmail(command.email());

        return userRegistrationValidator.validate(
                        command.numberDocument(),
                        normalizedEmail,
                        command.roleName()
                )
                .flatMap(rol -> {
                    User user = User.builder()
                            .firstName(command.firstName())
                            .lastName(command.lastName())
                            .numberDocument(command.numberDocument())
                            .phone(command.phone())
                            .birthDate(command.birthDate())
                            .email(normalizedEmail)
                            .password(passwordEncoderPort.encode(command.password()))
                            .status(true)
                            .role(rol)
                            .build();

                    return userPersistencePort.save(user);
                });
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
