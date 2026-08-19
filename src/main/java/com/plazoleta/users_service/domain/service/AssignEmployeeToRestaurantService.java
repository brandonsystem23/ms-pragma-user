package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.port.out.RestaurantEmployeePersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AssignEmployeeToRestaurantService {

    private final RestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    public Mono<Void> assign(Long ownerId, Long employeeId) {
        return restaurantEmployeePersistencePort.findRestaurantIdByOwnerId(ownerId)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.RESTAURANT_NOT_FOUND,
                        DomainErrorMessages.INVALID_RESTAURANT
                )))
                .flatMap(restaurantId ->
                        restaurantEmployeePersistencePort.assignEmployeeToRestaurant(restaurantId, employeeId)
                );
    }
}
