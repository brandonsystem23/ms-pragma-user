package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.domain.service.validation.DomainUserValidator;
import com.plazoleta.users_service.domain.service.validation.UserRegistrationValidator;
import com.plazoleta.users_service.domain.util.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRegistrationValidator userRegistrationValidator;
    private final DomainUserValidator domainUserValidator;
    private final AssignEmployeeToRestaurantService assignEmployeeToRestaurantService;

    @Override
    public Mono<User> register(RegisterUserCommand command, Long ownerId) {
        return Mono.defer(() -> {

            String normalizedEmail = EmailNormalizer.normalize(command.email());

            domainUserValidator.validateForRegister(command);

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
                    })
                    .flatMap(user -> {
                        if (ownerId != null) {
                            return assignEmployeeToRestaurantService
                                    .assign(ownerId, user.getId())
                                    .thenReturn(user);
                        }

                        return Mono.just(user);
                    });
        });
    }
}
