package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.mapper.ClientDtoMapper;
import com.plazoleta.users_service.application.mapper.EmployeeDtoMapper;
import com.plazoleta.users_service.application.mapper.OwnerDtoMapper;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserApplicationServiceTest {

    private RegisterUserUseCase registerUserUseCase;
    private UserDtoMapper userDtoMapper;
    private OwnerDtoMapper ownerDtoMapper;
    private EmployeeDtoMapper employeeDtoMapper;
    private ClientDtoMapper clientDtoMapper;
    private UserApplicationService userApplicationService;

    @BeforeEach
    void setUp() {
        registerUserUseCase = mock(RegisterUserUseCase.class);
        userDtoMapper = mock(UserDtoMapper.class);
        ownerDtoMapper = mock(OwnerDtoMapper.class);
        employeeDtoMapper = mock(EmployeeDtoMapper.class);
        clientDtoMapper = mock(ClientDtoMapper.class);
        userApplicationService = new UserApplicationService(registerUserUseCase, userDtoMapper, ownerDtoMapper,
                employeeDtoMapper, clientDtoMapper);
    }

    @Test
    void shouldCreateOwnerSuccessfully() {
        CreateOwnerRequest request = new CreateOwnerRequest(
                "Juan", "Perez", "123456", "+573001112233",
                LocalDate.of(1990, 1, 1),
                "owner@test.com", "123456"
        );

        User user = User.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("owner@test.com")
                .status(true)
                .role(Role.builder().name("PROPIETARIO").build())
                .build();

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

        when(registerUserUseCase.register(any())).thenReturn(Mono.just(user));
        when(userDtoMapper.toResponse(user)).thenReturn(response);

        StepVerifier.create(userApplicationService.createOwner(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Ana", "Lopez", "789456", "+573009998877",
                "employee@test.com", "123456"
        );

        User user = User.builder()
                .id(2L)
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .email("employee@test.com")
                .status(true)
                .role(Role.builder().name("EMPLEADO").build())
                .build();

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

        when(registerUserUseCase.register(any())).thenReturn(Mono.just(user));
        when(userDtoMapper.toResponse(user)).thenReturn(response);

        StepVerifier.create(userApplicationService.createEmployee(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldSelfRegisterClientSuccessfully() {
        CreateClientRequest request = new CreateClientRequest(
                "Carlos", "Ramirez", "456789", "+573007776655",
                "client@test.com", "123456"
        );

        User user = User.builder()
                .id(3L)
                .firstName("Carlos")
                .lastName("Ramirez")
                .numberDocument("456789")
                .phone("+573007776655")
                .email("client@test.com")
                .status(true)
                .role(Role.builder().name("CLIENTE").build())
                .build();

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

        when(registerUserUseCase.register(any())).thenReturn(Mono.just(user));
        when(userDtoMapper.toResponse(user)).thenReturn(response);

        StepVerifier.create(userApplicationService.selfRegisterClient(request))
                .expectNext(response)
                .verifyComplete();
    }
}
