package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.handler.IUserHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private IUserHandler userHandler;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldCreateOwnerSuccessfully() {
        CreateOwnerRequest request = new CreateOwnerRequest(
                "Juan",
                "Perez",
                "123456",
                "+573001112233",
                LocalDate.of(1990, Month.JANUARY, 1),
                "owner@test.com",
                "123456"
        );

        UserResponse response = UserResponse.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("owner@test.com")
                .status(true)
                .role("PROPIETARIO")
                .build();

        when(userHandler.createOwner(any())).thenReturn(Mono.just(response));

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

        Authentication authentication = new UsernamePasswordAuthenticationToken(5L, null);

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

        when(userHandler.createEmployee(any(), anyLong())).thenReturn(Mono.just(response));

        StepVerifier.create(userController.createEmployee(request, authentication))
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

        when(userHandler.selfRegisterClient(any())).thenReturn(Mono.just(response));

        StepVerifier.create(userController.selfRegisterClient(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldRetrieveUserSuccessfully() {
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

        when(userHandler.findUser(anyLong())).thenReturn(Mono.just(response));

        StepVerifier.create(userController.retrieveUser(1L))
                .expectNext(response)
                .verifyComplete();
    }
}
