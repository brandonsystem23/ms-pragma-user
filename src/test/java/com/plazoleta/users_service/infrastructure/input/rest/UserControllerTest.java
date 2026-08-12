package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.service.UserApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserApplicationService userApplicationService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldCreateOwnerSuccessfully() {
        CreateOwnerRequest request = new CreateOwnerRequest(
                "Juan",
                "Perez",
                "123456",
                "+573001112233",
                LocalDate.of(1990, 1, 1),
                "owner@test.com",
                "123456"
        );

        UserResponse response = UserResponse.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("owner@test.com")
                .status(true)
                .role("PROPIETARIO")
                .build();

        when(userApplicationService.createOwner(any())).thenReturn(Mono.just(response));

        StepVerifier.create(userController.createOwner(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Ana",
                "Lopez",
                "789456",
                "+573009998877",
                "employee@test.com",
                "123456"
        );

        UserResponse response = UserResponse.builder()
                .id(2L)
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .email("employee@test.com")
                .status(true)
                .role("EMPLEADO")
                .build();

        when(userApplicationService.createEmployee(any())).thenReturn(Mono.just(response));

        StepVerifier.create(userController.createEmployee(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldSelfRegisterClientSuccessfully() {
        CreateClientRequest request = new CreateClientRequest(
                "Carlos",
                "Ramirez",
                "456789",
                "+573007776655",
                "client@test.com",
                "123456"
        );

        UserResponse response = UserResponse.builder()
                .id(3L)
                .firstName("Carlos")
                .lastName("Ramirez")
                .numberDocument("456789")
                .phone("+573007776655")
                .email("client@test.com")
                .status(true)
                .role("CLIENTE")
                .build();

        when(userApplicationService.selfRegisterClient(any())).thenReturn(Mono.just(response));

        StepVerifier.create(userController.selfRegisterClient(request))
                .expectNext(response)
                .verifyComplete();
    }
}
