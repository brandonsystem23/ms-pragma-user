package com.plazoleta.users_service.domain.spi;

import com.plazoleta.users_service.domain.model.RestaurantEmployee;
import reactor.core.publisher.Mono;

public interface IRestaurantEmployeePersistencePort {

    Mono<Long> findRestaurantIdByOwnerId(Long ownerId);

    Mono<Void> assignEmployeeToRestaurant(RestaurantEmployee restaurantEmployee);
}
