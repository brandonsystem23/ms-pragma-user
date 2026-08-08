package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

class RegisterUserServiceTest {

    private UserPersistencePort userPersistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private UserRegistrationValidator userRegistrationValidator;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        userPersistencePort = Mockito.mock(UserPersistencePort.class);
        passwordEncoderPort = Mockito.mock(PasswordEncoderPort.class);
        userRegistrationValidator = Mockito.mock(UserRegistrationValidator.class);

        registerUserService = new RegisterUserService(
                userPersistencePort,
                passwordEncoderPort,
                userRegistrationValidator
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Pérez",
                "123456789",
                "+573001234567",
                LocalDate.of(1990, 1, 1),
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
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("juan@mail.com")
                .password("encoded-password")
                .status(true)
                .role(role)
                .build();

        Mockito.when(userRegistrationValidator.validate(
                        "123456789",
                        "juan@mail.com",
                        "PROPIETARIO"
                ))
                .thenReturn(Mono.just(role));

        Mockito.when(passwordEncoderPort.encode("123456"))
                .thenReturn("encoded-password");

        Mockito.when(userPersistencePort.save(Mockito.any(User.class)))
                .thenReturn(Mono.just(savedUser));

        StepVerifier.create(registerUserService.register(command))
                .expectNextMatches(user ->
                        user.getId().equals(10L) &&
                                user.getEmail().equals("juan@mail.com") &&
                                user.getPassword().equals("encoded-password") &&
                                user.getRole().getName().equals("PROPIETARIO")
                )
                .verifyComplete();

        Mockito.verify(userRegistrationValidator).validate(
                "123456789",
                "juan@mail.com",
                "PROPIETARIO"
        );
        Mockito.verify(passwordEncoderPort).encode("123456");
        Mockito.verify(userPersistencePort).save(Mockito.any(User.class));
    }
}
