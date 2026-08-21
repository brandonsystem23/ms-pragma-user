package com.plazoleta.users_service.domain.validation;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainErrorMessages;
import com.plazoleta.users_service.domain.exception.DomainException;
import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.domain.validation.UserRegistrationValidator;
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
    private IUserPersistencePort userPersistencePort;

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
        Role role = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(userPersistencePort.existsByNumberDocument(anyString()))
                .thenReturn(Mono.just(true));
        when(userPersistencePort.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.findRoleByName(anyString()))
                .thenReturn(Mono.just(role));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);
                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.DUPLICATE_DOCUMENT, exception.getCode());
                    Assertions.assertEquals(DomainErrorMessages.DUPLICATE_DOCUMENT, exception.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        Role role = Role.builder()
                .id(1L)
                .name("CLIENTE")
                .description("Rol cliente")
                .build();

        when(userPersistencePort.existsByNumberDocument(anyString()))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.existsByEmail(anyString()))
                .thenReturn(Mono.just(true));
        when(userPersistencePort.findRoleByName(anyString()))
                .thenReturn(Mono.just(role));

        StepVerifier.create(validator.validate("123456", "test@test.com", "CLIENTE"))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);
                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.DUPLICATE_EMAIL, exception.getCode());
                    Assertions.assertEquals(DomainErrorMessages.DUPLICATE_EMAIL, exception.getMessage());
                })
                .verify();
    }

    @Test
    void shouldFailWhenRoleDoesNotExist() {
        String numberDocument = "12345678";
        String email = "test@mail.com";
        String roleName = "ROLE_INEXISTENTE";

        when(userPersistencePort.existsByNumberDocument(numberDocument))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.existsByEmail(email))
                .thenReturn(Mono.just(false));
        when(userPersistencePort.findRoleByName(roleName))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate(numberDocument, email, roleName))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(DomainException.class, error);

                    DomainException exception = (DomainException) error;
                    Assertions.assertEquals(DomainErrorCode.ROLE_NOT_FOUND, exception.getCode());
                    Assertions.assertEquals("El rol ROLE_INEXISTENTE no existe", exception.getMessage());
                })
                .verify();
    }
}