package com.plazoleta.users_service.domain.usecase;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.service.AssignEmployeeService;
import com.plazoleta.users_service.domain.spi.IPasswordEncoderPort;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.domain.service.validation.DomainUserValidator;
import com.plazoleta.users_service.domain.service.validation.UserRegistrationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private IUserPersistencePort iUserPersistencePort;

    @Mock
    private IPasswordEncoderPort iPasswordEncoderPort;

    @Mock
    private UserRegistrationValidator userRegistrationValidator;

    @Mock
    private AssignEmployeeService assignEmployeeService;

    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        registerUserUseCase = new RegisterUserUseCase(
                iUserPersistencePort,
                iPasswordEncoderPort,
                userRegistrationValidator,
                new DomainUserValidator(),
                assignEmployeeService
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Pérez",
                "123456789",
                "+573001234567",
                LocalDate.of(1990, Month.JANUARY, 1),
                "JUAN@MAIL.COM",
                "123456",
                "PROPIETARIO"
        );

        Role role = Role.builder()
                .id(1L)
                .name("PROPIETARIO")
                .description("Rol propietario")
                .build();

        User savedUser = User.builder()
                .id(10L)
                .firstName("Juan")
                .lastName("Pérez")
                .numberDocument("123456789")
                .phone("+573001234567")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("juan@mail.com")
                .password("encoded-password")
                .status(true)
                .role(role)
                .build();

        when(userRegistrationValidator.validate(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(role));

        when(iPasswordEncoderPort.encode(anyString())).thenReturn("encoded-password");

        when(iUserPersistencePort.save(any())).thenReturn(Mono.just(savedUser));

        StepVerifier.create(registerUserUseCase.register(command, null))
                .assertNext(user -> {
                    Assertions.assertEquals(10L, user.getId());
                    Assertions.assertEquals("juan@mail.com", user.getEmail());
                    Assertions.assertEquals("encoded-password", user.getPassword());
                    Assertions.assertEquals("PROPIETARIO", user.getRole().getName());
                })
                .verifyComplete();
    }

    @Test
    void shouldRegisterUserEmployeeSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Pérez",
                "123456789",
                "+573001234567",
                LocalDate.of(1990, Month.JANUARY, 1),
                "JUAN@MAIL.COM",
                "123456",
                "EMPLEADO"
        );

        Role role = Role.builder()
                .id(1L)
                .name("EMPLEADO")
                .description("Rol EMPLEADO")
                .build();

        User savedUser = User.builder()
                .id(10L)
                .firstName("Juan")
                .lastName("Pérez")
                .numberDocument("123456789")
                .phone("+573001234567")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("juan@mail.com")
                .password("encoded-password")
                .status(true)
                .role(role)
                .build();

        when(userRegistrationValidator.validate(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(role));

        when(iPasswordEncoderPort.encode(anyString()))
                .thenReturn("encoded-password");

        when(iUserPersistencePort.save(any()))
                .thenReturn(Mono.just(savedUser));

        when(assignEmployeeService.validateOwnerHasRestaurant(anyLong()))
                .thenReturn(Mono.just(1L));

        when(assignEmployeeService.assignToRestaurant(anyLong(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(registerUserUseCase.register(command, 1L))
                .assertNext(user -> {
                    Assertions.assertEquals(10L, user.getId());
                    Assertions.assertEquals("juan@mail.com", user.getEmail());
                    Assertions.assertEquals("encoded-password", user.getPassword());
                    Assertions.assertEquals("EMPLEADO", user.getRole().getName());
                })
                .verifyComplete();
    }
}
