package com.plazoleta.users_service.domain.validation.user;

import com.plazoleta.users_service.domain.builder.UserBuilder;
import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.RestaurantEmployee;
import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AssignerRestaurantValidator {

    private final IRestaurantEmployeePersistencePort iRestaurantEmployeePersistencePort;

    public Mono<Long> validateOwnerHasRestaurant(Long ownerId) {
        return iRestaurantEmployeePersistencePort.findRestaurantIdByOwnerId(ownerId)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.RESTAURANT_NOT_FOUND,
                        DomainErrorMessages.INVALID_RESTAURANT
                )));
    }

    public Mono<Void> assignToRestaurant(Long restaurantId, Long employeeId) {

        RestaurantEmployee restaurantEmployee = UserBuilder.buildRestaurantEmployee(restaurantId, employeeId);

        return iRestaurantEmployeePersistencePort.assignEmployeeToRestaurant(restaurantEmployee);
    }
}
