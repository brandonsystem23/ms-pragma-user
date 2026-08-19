package com.plazoleta.users_service.infrastructure.output.postgres.adapter;

import com.plazoleta.users_service.domain.port.out.RestaurantEmployeePersistencePort;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RestaurantEmployeeEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.RestaurantEmployeeRepository;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RestaurantEmployeePersistenceAdapter implements RestaurantEmployeePersistencePort {

    private final UserRepository userRepository;
    private final RestaurantEmployeeRepository restaurantEmployeeRepository;

    @Override
    public Mono<Long> findRestaurantIdByOwnerId(Long ownerId) {
        return userRepository.findRestaurantIdByOwnerId(ownerId);
    }

    @Override
    public Mono<Void> assignEmployeeToRestaurant(Long restaurantId, Long employeeId) {
        RestaurantEmployeeEntity entity = RestaurantEmployeeEntity.builder()
                .restaurantId(restaurantId)
                .employeeId(employeeId)
                .build();

        return restaurantEmployeeRepository.save(entity).then();
    }
}
