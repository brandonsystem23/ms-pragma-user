package com.plazoleta.users_service.infrastructure.out.postgres.repository;

import com.plazoleta.users_service.infrastructure.out.postgres.entity.RestaurantEmployeeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IRestaurantEmployeeRepository extends ReactiveCrudRepository<RestaurantEmployeeEntity, Long> {
}
