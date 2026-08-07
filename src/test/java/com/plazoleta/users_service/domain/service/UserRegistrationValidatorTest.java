package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.domain.port.out.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRegistrationValidatorTest {

    private UsuarioPersistencePort usuarioPersistencePort;
    private UserRegistrationValidator validator;

    @BeforeEach
    void setUp() {
        usuarioPersistencePort = mock(UsuarioPersistencePort.class);
        validator = new UserRegistrationValidator(usuarioPersistencePort);
    }

    @Test
    void shouldValidateSuccessfully() {
        Rol rol = Rol.builder()
                .id(1L)
                .nombre("CLIENTE")
                .descripcion("Rol cliente")
                .build();

        when(usuarioPersistencePort.existsByDocumento("123456"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByCorreo("test@test.com"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByNombre("CLIENTE"))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectNext(rol)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenDocumentAlreadyExists() {
        Rol rol = Rol.builder()
                .id(1L)
                .nombre("CLIENTE")
                .descripcion("Rol cliente")
                .build();

        when(usuarioPersistencePort.existsByDocumento("123456"))
                .thenReturn(Mono.just(true));
        when(usuarioPersistencePort.existsByCorreo("test@test.com"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByNombre("CLIENTE"))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(DuplicateDocumentException.class)
                .verify();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        Rol rol = Rol.builder()
                .id(1L)
                .nombre("CLIENTE")
                .descripcion("Rol cliente")
                .build();

        when(usuarioPersistencePort.existsByDocumento("123456"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByCorreo("test@test.com"))
                .thenReturn(Mono.just(true));
        when(usuarioPersistencePort.findRoleByNombre("CLIENTE"))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(DuplicateEmailException.class)
                .verify();
    }

    @Test
    void shouldFailWhenRoleDoesNotExist() {
        when(usuarioPersistencePort.existsByDocumento("123456"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByCorreo("test@test.com"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByNombre("CLIENTE"))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(RoleNotFoundException.class)
                .verify();
    }
}
