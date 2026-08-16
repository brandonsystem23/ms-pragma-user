package com.plazoleta.users_service.domain.service;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveUserServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private RetrieveUserService retrieveUserService;

    @Test
    void shouldRetrieveUserSuccessfully() {

        Role role = Role.builder()
                .id(1L)
                .name("PROPIETARIO")
                .description("Rol propietario")
                .build();

        User userResponse = User.builder()
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


        when(userPersistencePort.findById(anyLong())).thenReturn(Mono.just(userResponse));

        StepVerifier.create(retrieveUserService.find(1L))
                .assertNext(user -> {
                    Assertions.assertEquals(10L, user.getId());
                    Assertions.assertEquals("juan@mail.com", user.getEmail());
                    Assertions.assertEquals("encoded-password", user.getPassword());
                    Assertions.assertEquals("PROPIETARIO", user.getRole().getName());
                })
                .verifyComplete();
    }
}
