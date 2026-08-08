package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    private UserPersistencePort userPersistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        userPersistencePort = mock(UserPersistencePort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        registerUserService = new RegisterUserService(userPersistencePort, passwordEncoderPort);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        Role role = Role.builder()
                .id(2L)
                .name("PROPIETARIO")
                .description("Rol propietario")
                .build();

        RegisterUserCommand command = new RegisterUserCommand(
                "Juan",
                "Pérez",
                "123456",
                "+573001112233",
                LocalDate.of(1990, 1, 1),
                "Juan@Email.com ",
                "123456",
                "PROPIETARIO"
        );

        when(userPersistencePort.existsByNumberDocument("123456")).thenReturn(Mono.just(false));
        when(userPersistencePort.existsByEmail("juan@email.com")).thenReturn(Mono.just(false));
        when(userPersistencePort.findRoleByName("PROPIETARIO")).thenReturn(Mono.just(role));
        when(passwordEncoderPort.encode("123456")).thenReturn("encoded-password");

        when(userPersistencePort.save(any(User.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(registerUserService.register(command))
                .assertNext(user -> {
                    assert "Juan".equals(user.getFirstName());
                    assert "Pérez".equals(user.getLastName());
                    assert "123456".equals(user.getNumberDocument());
                    assert "+573001112233".equals(user.getPhone());
                    assert LocalDate.of(1990, 1, 1).equals(user.getBirthDate());
                    assert "juan@email.com".equals(user.getEmail());
                    assert "encoded-password".equals(user.getPassword());
                    assert Boolean.TRUE.equals(user.getStatus());
                    assert "PROPIETARIO".equals(user.getRole().getName());
                })
                .verifyComplete();
    }
}
