package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.spi.IRestaurantEmployeePersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignEmployeeServiceTest {

    @Mock
    private IRestaurantEmployeePersistencePort iRestaurantEmployeePersistencePort;

    @InjectMocks
    private AssignEmployeeService assignEmployeeService;


    @Test
    void shouldValidateOwnerHasRestaurantSuccessfully() {

        when(iRestaurantEmployeePersistencePort.findRestaurantIdByOwnerId(anyLong()))
                .thenReturn(Mono.just(20L));

        StepVerifier.create(assignEmployeeService.validateOwnerHasRestaurant(5L))
                .expectNext(20L)
                .verifyComplete();

    }

    @Test
    void shouldFailWhenOwnerDoesNotHaveRestaurant() {

        when(iRestaurantEmployeePersistencePort.findRestaurantIdByOwnerId(anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(assignEmployeeService.validateOwnerHasRestaurant(5L))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;

                    Assertions.assertEquals(
                            DomainErrorCode.RESTAURANT_NOT_FOUND,
                            exception.getCode()
                    );

                    Assertions.assertEquals(
                            DomainErrorMessages.INVALID_RESTAURANT,
                            exception.getMessage()
                    );
                })
                .verify();

    }

    @Test
    void shouldAssignEmployeeToRestaurantSuccessfully() {

        when(iRestaurantEmployeePersistencePort.assignEmployeeToRestaurant(anyLong(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(assignEmployeeService.assignToRestaurant(20L, 10L))
                .verifyComplete();

    }
}