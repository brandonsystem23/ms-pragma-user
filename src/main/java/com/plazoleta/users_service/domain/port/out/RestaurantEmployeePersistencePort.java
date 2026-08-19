package com.plazoleta.users_service.domain.port.out;

import reactor.core.publisher.Mono;

public interface RestaurantEmployeePersistencePort {

    Mono<Long> findRestaurantIdByOwnerId(Long ownerId);

    Mono<Void> assignEmployeeToRestaurant(Long restaurantId, Long employeeId);
}
