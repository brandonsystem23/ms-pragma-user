package com.plazoleta.users_service.infrastructure.out.postgres.adapter;

import com.plazoleta.users_service.domain.model.RestaurantEmployee;
import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.users_service.infrastructure.out.postgres.mapper.UserEntityMapper;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IRestaurantEmployeeRepository;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RestaurantEmployeePersistenceAdapter implements IRestaurantEmployeePersistencePort {

    private final IUserRepository iUserRepository;
    private final IRestaurantEmployeeRepository iRestaurantEmployeeRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Mono<Long> findRestaurantIdByOwnerId(Long ownerId) {

        log.info("Busqueda del id de restaurante para owner con id={}", ownerId);

        return iUserRepository.findRestaurantIdByOwnerId(ownerId);
    }

    @Override
    public Mono<Void> assignEmployeeToRestaurant(RestaurantEmployee restaurantEmployee) {

        log.info("Asignar empleado con id={} a restaurante con id={}", restaurantEmployee.employeeId(),
                restaurantEmployee.restaurantId());

        return iRestaurantEmployeeRepository.save(
                userEntityMapper.toEntityRestaurant(restaurantEmployee)
                )
                .then();
    }
}
