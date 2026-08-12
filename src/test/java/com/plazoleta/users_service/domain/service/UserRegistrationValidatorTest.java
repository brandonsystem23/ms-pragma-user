package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationValidatorTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private UserRegistrationValidator validator;


    @Test
    void shouldValidateSuccessfully() {
        Role role = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(userPersistencePort.existsByNumberDocument(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.findRoleByName(anyString()))
                .thenReturn(Mono.just(role));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .assertNext(response ->
                    Assertions.assertEquals("CLIENTE", response.getName())
                )
                .verifyComplete();
    }

    @Test
    void shouldFailWhenDocumentAlreadyExists() {
        Role rol = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(userPersistencePort.existsByNumberDocument(anyString()))
                .thenReturn(Mono.just(true));
        when(userPersistencePort.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.findRoleByName(anyString()))
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

        when(userPersistencePort.existsByNumberDocument(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.existsByEmail(anyString()))
                .thenReturn(Mono.just(true));
        when(userPersistencePort.findRoleByName(anyString()))
                .thenReturn(Mono.just(rol));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(DuplicateEmailException.class)
                .verify();
    }

    @Test
    void shouldFailWhenRoleDoesNotExist() {
        when(userPersistencePort.existsByNumberDocument(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.findRoleByName(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectError(RoleNotFoundException.class)
                .verify();
    }
}
