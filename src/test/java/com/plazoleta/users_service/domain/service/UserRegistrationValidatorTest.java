package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRegistrationValidatorTest {

    private UserPersistencePort usuarioPersistencePort;
    private UserRegistrationValidator validator;

    @BeforeEach
    void setUp() {
        usuarioPersistencePort = mock(UserPersistencePort.class);
        validator = new UserRegistrationValidator(usuarioPersistencePort);
    }

    @Test
    void shouldValidateSuccessfully() {
        Role rol = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(usuarioPersistencePort.existsByNumberDocument("123456"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByEmail("test@test.com"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByFirstName("CLIENTE"))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectNext(rol)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenDocumentAlreadyExists() {
        Role rol = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(usuarioPersistencePort.existsByNumberDocument("123456"))
                .thenReturn(Mono.just(true));
        when(usuarioPersistencePort.existsByEmail("test@test.com"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByFirstName("CLIENTE"))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(DuplicateDocumentException.class)
                .verify();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        Role rol = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(usuarioPersistencePort.existsByNumberDocument("123456"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByEmail("test@test.com"))
                .thenReturn(Mono.just(true));
        when(usuarioPersistencePort.findRoleByFirstName("CLIENTE"))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(DuplicateEmailException.class)
                .verify();
    }

    @Test
    void shouldFailWhenRoleDoesNotExist() {
        when(usuarioPersistencePort.existsByNumberDocument("123456"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.existsByEmail("test@test.com"))
                .thenReturn(Mono.just(false));
        when(usuarioPersistencePort.findRoleByFirstName("CLIENTE"))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(RoleNotFoundException.class)
                .verify();
    }
}
