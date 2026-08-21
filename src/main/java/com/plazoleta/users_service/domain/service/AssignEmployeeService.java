package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AssignEmployeeService {

    private final IRestaurantEmployeePersistencePort iRestaurantEmployeePersistencePort;

    public Mono<Long> validateOwnerHasRestaurant(Long ownerId) {
        return iRestaurantEmployeePersistencePort.findRestaurantIdByOwnerId(ownerId)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.RESTAURANT_NOT_FOUND,
                        DomainErrorMessages.INVALID_RESTAURANT
                )));
    }

    public Mono<Void> assignToRestaurant(Long restaurantId, Long employeeId) {
        return iRestaurantEmployeePersistencePort.assignEmployeeToRestaurant(restaurantId, employeeId);
    }
}
