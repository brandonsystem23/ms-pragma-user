package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.api.IUserRegisterServicePort;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.service.AssignEmployeeService;
import com.plazoleta.users_service.domain.service.EmailNormalizer;
import com.plazoleta.users_service.domain.validation.DomainUserValidator;
import com.plazoleta.users_service.domain.validation.UserRegistrationValidator;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase implements IUserRegisterServicePort {

    private final IUserPersistencePort iUserPersistencePort;
    private final IPasswordEncoderPort iPasswordEncoderPort;
    private final UserRegistrationValidator userRegistrationValidator;
    private final DomainUserValidator domainUserValidator;
    private final AssignEmployeeService assignEmployeeService;

    @Override
    public Mono<User> register(RegisterUserCommand command, Long ownerId) {
        return Mono.defer(() -> {
            String normalizedEmail = EmailNormalizer.normalize(command.email());

            domainUserValidator.validateForRegister(command);

            Mono<Long> restaurantIdMono = (ownerId != null) ? assignEmployeeService.validateOwnerHasRestaurant(ownerId)
                    : Mono.empty();

            return userRegistrationValidator.validate(
                            command.numberDocument(),
                            normalizedEmail,
                            command.roleName()
                    )
                    .flatMap(role -> {
                        if (ownerId != null) {
                            return restaurantIdMono.flatMap(restaurantId ->
                                    saveUser(command, normalizedEmail, role)
                                            .flatMap(user -> assignEmployeeService
                                                    .assignToRestaurant(restaurantId, user.getId())
                                                    .thenReturn(user))
                            );
                        }

                        return saveUser(command, normalizedEmail, role);
                    });
        });
    }

    private Mono<User> saveUser(RegisterUserCommand command, String normalizedEmail, Role role) {
        User user = User.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .numberDocument(command.numberDocument())
                .phone(command.phone())
                .birthDate(command.birthDate())
                .email(normalizedEmail)
                .password(iPasswordEncoderPort.encode(command.password()))
                .status(true)
                .role(role)
                .build();

        return iUserPersistencePort.save(user);
    }
}
