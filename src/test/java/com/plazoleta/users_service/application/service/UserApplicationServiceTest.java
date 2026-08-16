package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.in.RetrieveUserCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private RetrieveUserCase retrieveUserCase;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UserApplicationService userApplicationService;

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

        UserResponse responseUser = UserResponse.builder()
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
        when(userDtoMapper.toResponse(any())).thenReturn(responseUser);

        StepVerifier.create(userApplicationService.createOwner(request))
                .assertNext(response ->
                    Assertions.assertEquals("PROPIETARIO", response.role())
                )
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

        UserResponse responseUser = UserResponse.builder()
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
        when(userDtoMapper.toResponse(any())).thenReturn(responseUser);

        StepVerifier.create(userApplicationService.createEmployee(request))
                .assertNext(response ->
                    Assertions.assertEquals("EMPLEADO", response.role())
                )
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

        UserResponse responseUser = UserResponse.builder()
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
        when(userDtoMapper.toResponse(user)).thenReturn(responseUser);


        StepVerifier.create(userApplicationService.selfRegisterClient(request))
                .assertNext(response ->
                    Assertions.assertEquals("CLIENTE", response.role())
                )
                .verifyComplete();
    }

    @Test
    void shouldFindUserSuccessfully() {
        User user = User.builder()
                .id(3L)
                .firstName("Carlos")
                .lastName("Ramirez")
                .numberDocument("456789")
                .phone("+573007776655")
                .email("client@test.com")
                .status(true)
                .role(Role.builder().name("PROPIETARIO").build())
                .build();

        UserResponse responseUser = UserResponse.builder()
                .id(3L)
                .firstName("Carlos")
                .lastName("Ramirez")
                .numberDocument("456789")
                .phone("+573007776655")
                .email("client@test.com")
                .status(true)
                .role("PROPIETARIO")
                .build();

        when(retrieveUserCase.find(anyLong())).thenReturn(Mono.just(user));
        when(userDtoMapper.toResponse(user)).thenReturn(responseUser);


        StepVerifier.create(userApplicationService.findUser(1L))
                .assertNext(response ->
                        Assertions.assertEquals("PROPIETARIO", response.role())
                )
                .verifyComplete();
    }
}
