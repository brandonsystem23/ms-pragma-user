package com.plazoleta.users_service.infrastructure.out.postgres.adapter;

import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import com.plazoleta.users_service.infrastructure.out.postgres.entity.RestaurantEmployeeEntity;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IRestaurantEmployeeRepository;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RestaurantEmployeePersistenceAdapter implements IRestaurantEmployeePersistencePort {

    private final IUserRepository iUserRepository;
    private final IRestaurantEmployeeRepository iRestaurantEmployeeRepository;

    @Override
    public Mono<Long> findRestaurantIdByOwnerId(Long ownerId) {
        return iUserRepository.findRestaurantIdByOwnerId(ownerId);
    }

    @Override
    public Mono<Void> assignEmployeeToRestaurant(Long restaurantId, Long employeeId) {
        RestaurantEmployeeEntity entity = RestaurantEmployeeEntity.builder()
                .restaurantId(restaurantId)
                .employeeId(employeeId)
                .build();

        return iRestaurantEmployeeRepository.save(entity).then();
    }
}
