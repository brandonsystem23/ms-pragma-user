package com.plazoleta.users_service.infrastructure.output.postgres.adapter;

import com.plazoleta.users_service.infrastructure.output.postgres.entity.RestaurantEmployeeEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.RestaurantEmployeeRepository;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantEmployeePersistenceAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantEmployeeRepository restaurantEmployeeRepository;

    @InjectMocks
    private RestaurantEmployeePersistenceAdapter adapter;

    @Test
    void shouldFindRestaurantIdByOwnerIdSuccessfully() {
        when(userRepository.findRestaurantIdByOwnerId(5L))
                .thenReturn(Mono.just(15L));

        StepVerifier.create(adapter.findRestaurantIdByOwnerId(5L))
                .assertNext(id -> assertEquals(15L, id))
                .verifyComplete();
    }

    @Test
    void shouldAssignEmployeeToRestaurantSuccessfully() {
        RestaurantEmployeeEntity entity = RestaurantEmployeeEntity.builder()
                .id(1L)
                .restaurantId(15L)
                .employeeId(10L)
                .build();

        when(restaurantEmployeeRepository.save(ArgumentMatchers.any(RestaurantEmployeeEntity.class)))
                .thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.assignEmployeeToRestaurant(15L, 10L))
                .verifyComplete();
    }
}
