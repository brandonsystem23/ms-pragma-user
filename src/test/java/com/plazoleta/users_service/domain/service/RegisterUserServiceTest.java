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

    private UserPersistencePort usuarioPersistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        usuarioPersistencePort = mock(UserPersistencePort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        registerUserService = new RegisterUserService(usuarioPersistencePort, passwordEncoderPort);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        Role rol = Role.builder()
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

        when(usuarioPersistencePort.existsByNumberDocument("123456")).thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByEmail("juan@email.com")).thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByFirstName("PROPIETARIO")).thenReturn(Mono.just(rol));
        when(passwordEncoderPort.encode("123456")).thenReturn("encoded-password");

        when(usuarioPersistencePort.save(any(User.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(registerUserService.register(command))
                .assertNext(usuario -> {
                    assert "Juan".equals(usuario.getFirstName());
                    assert "Pérez".equals(usuario.getLastName());
                    assert "123456".equals(usuario.getNumberDocument());
                    assert "+573001112233".equals(usuario.getPhone());
                    assert LocalDate.of(1990, 1, 1).equals(usuario.getBirthDate());
                    assert "juan@email.com".equals(usuario.getEmail());
                    assert "encoded-password".equals(usuario.getPassword());
                    assert Boolean.TRUE.equals(usuario.getStatus());
                    assert "PROPIETARIO".equals(usuario.getRole().getName());
                })
                .verifyComplete();
    }
}
