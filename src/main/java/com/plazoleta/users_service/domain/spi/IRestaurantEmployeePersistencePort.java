package com.plazoleta.users_service.domain.spi;

import reactor.core.publisher.Mono;

public interface IRestaurantEmployeePersistencePort {

    Mono<Long> findRestaurantIdByOwnerId(Long ownerId);

    Mono<Void> assignEmployeeToRestaurant(Long restaurantId, Long employeeId);
}
