package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.domain.util.EmailNormalizer;
import reactor.core.publisher.Mono;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRegistrationValidator userRegistrationValidator;

    public RegisterUserService(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            UserRegistrationValidator userRegistrationValidator
    ) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userRegistrationValidator = userRegistrationValidator;
    }

    @Override
    public Mono<User> register(RegisterUserCommand command) {
        String normalizedEmail = EmailNormalizer.normalize(command.email());

        return userRegistrationValidator.validate(
                        command.numberDocument(),
                        normalizedEmail,
                        command.roleName()
                )
                .flatMap(role -> {
                    User user = User.builder()
                            .firstName(command.firstName())
                            .lastName(command.lastName())
                            .numberDocument(command.numberDocument())
                            .phone(command.phone())
                            .birthDate(command.birthDate())
                            .email(normalizedEmail)
                            .password(passwordEncoderPort.encode(command.password()))
                            .status(true)
                            .role(role)
                            .build();

                    return userPersistencePort.save(user);
                });
    }
}
