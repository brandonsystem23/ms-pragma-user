package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.out.PasswordEncoderPort;
import com.plazoleta.users_service.domain.port.out.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    private UsuarioPersistencePort usuarioPersistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        usuarioPersistencePort = mock(UsuarioPersistencePort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        registerUserService = new RegisterUserService(usuarioPersistencePort, passwordEncoderPort);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        Rol rol = Rol.builder()
                .id(2L)
                .nombre("PROPIETARIO")
                .descripcion("Rol propietario")
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

        when(usuarioPersistencePort.existsByDocumento("123456")).thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByCorreo("juan@email.com")).thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByNombre("PROPIETARIO")).thenReturn(Mono.just(rol));
        when(passwordEncoderPort.encode("123456")).thenReturn("encoded-password");

        when(usuarioPersistencePort.save(any(Usuario.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(registerUserService.register(command))
                .assertNext(usuario -> {
                    assert "Juan".equals(usuario.getNombre());
                    assert "Pérez".equals(usuario.getApellido());
                    assert "123456".equals(usuario.getDocumentoIdentidad());
                    assert "+573001112233".equals(usuario.getTelefono());
                    assert LocalDate.of(1990, 1, 1).equals(usuario.getFechaNacimiento());
                    assert "juan@email.com".equals(usuario.getCorreo());
                    assert "encoded-password".equals(usuario.getPassword());
                    assert Boolean.TRUE.equals(usuario.getActivo());
                    assert "PROPIETARIO".equals(usuario.getRol().getNombre());
                })
                .verifyComplete();
    }
}
