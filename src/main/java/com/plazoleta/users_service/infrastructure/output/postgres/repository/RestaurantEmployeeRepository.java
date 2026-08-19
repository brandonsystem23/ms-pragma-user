package com.plazoleta.users_service.infrastructure.output.postgres.repository;

import com.plazoleta.users_service.infrastructure.output.postgres.entity.RestaurantEmployeeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RestaurantEmployeeRepository extends ReactiveCrudRepository<RestaurantEmployeeEntity, Long> {
}
