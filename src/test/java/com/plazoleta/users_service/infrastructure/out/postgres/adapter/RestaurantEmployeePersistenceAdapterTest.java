package com.plazoleta.users_service.infrastructure.out.postgres.adapter;

import com.plazoleta.users_service.domain.model.RestaurantEmployee;
import com.plazoleta.users_service.infrastructure.out.postgres.entity.RestaurantEmployeeEntity;
import com.plazoleta.users_service.infrastructure.out.postgres.mapper.UserEntityMapper;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IRestaurantEmployeeRepository;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantEmployeePersistenceAdapterTest {

    @Mock
    private IUserRepository iUserRepository;

    @Mock
    private IRestaurantEmployeeRepository iRestaurantEmployeeRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private RestaurantEmployeePersistenceAdapter adapter;

    @Test
    void shouldFindRestaurantIdByOwnerIdSuccessfully() {
        when(iUserRepository.findRestaurantIdByOwnerId(5L))
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

        RestaurantEmployee restaurantEmployee = RestaurantEmployee.builder()
                .restaurantId(15L)
                .employeeId(10L)
                .build();

        when(iRestaurantEmployeeRepository.save(any(RestaurantEmployeeEntity.class)))
                .thenReturn(Mono.just(entity));

        when(userEntityMapper.toEntityRestaurant(any())).thenReturn(entity);

        StepVerifier.create(adapter.assignEmployeeToRestaurant(restaurantEmployee))
                .verifyComplete();
    }
}
