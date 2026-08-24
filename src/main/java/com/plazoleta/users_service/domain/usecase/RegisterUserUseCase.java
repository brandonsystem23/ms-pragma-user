package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.api.IUserRegisterServicePort;
import com.plazoleta.users_service.domain.builder.UserBuilder;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.validation.user.AssignerRestaurantValidator;
import com.plazoleta.users_service.domain.validation.EmailNormalizer;
import com.plazoleta.users_service.domain.validation.user.DomainUserValidator;
import com.plazoleta.users_service.domain.validation.user.UserRegistrationValidator;
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
    private final AssignerRestaurantValidator assignerRestaurantValidator;

    @Override
    public Mono<User> register(RegisterUserCommand command, Long ownerId) {
        return Mono.defer(() -> {

            String normalizedEmail = EmailNormalizer.normalize(command.email());

            domainUserValidator.validateForRegister(command);

            Mono<Long> restaurantIdMono = (ownerId != null) ? assignerRestaurantValidator
                    .validateOwnerHasRestaurant(ownerId) : Mono.empty();

            return userRegistrationValidator.validate(
                            command.numberDocument(),
                            normalizedEmail,
                            command.roleName()
                    )
                    .flatMap(role -> {
                        String passwordEncode = iPasswordEncoderPort.encode(command.password());
                        User user = UserBuilder.buildUser(command, normalizedEmail, role, passwordEncode);
                        if (ownerId != null) {
                            return restaurantIdMono.flatMap(restaurantId ->
                                    iUserPersistencePort.save(user)
                                            .flatMap(userSave -> assignerRestaurantValidator
                                                    .assignToRestaurant(restaurantId, userSave.getId())
                                                    .thenReturn(userSave))
                            );
                        }

                        return iUserPersistencePort.save(user);
                    });
        });
    }


}
