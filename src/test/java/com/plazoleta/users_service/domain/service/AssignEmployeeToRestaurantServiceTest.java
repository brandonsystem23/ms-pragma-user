package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.port.out.RestaurantEmployeePersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignEmployeeToRestaurantServiceTest {

    @Mock
    private RestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    private AssignEmployeeToRestaurantService service;

    @BeforeEach
    void setUp() {
        service = new AssignEmployeeToRestaurantService(restaurantEmployeePersistencePort);
    }

    @Test
    void shouldAssignEmployeeToRestaurantSuccessfully() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByOwnerId(5L))
                .thenReturn(Mono.just(20L));

        when(restaurantEmployeePersistencePort.assignEmployeeToRestaurant(20L, 10L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.assign(5L, 10L))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenRestaurantNotFoundForOwner() {
        when(restaurantEmployeePersistencePort.findRestaurantIdByOwnerId(5L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.assign(5L, 10L))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);
                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.RESTAURANT_NOT_FOUND, exception.getCode());
                    Assertions.assertEquals("No se encontró un restaurante asociado al propietario", exception.getMessage());
                })
                .verify();
    }
}
