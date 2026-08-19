package com.plazoleta.users_service.infrastructure.output.postgres.adapter;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RoleEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.UserEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.mapper.UserEntityMapper;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.RoleRepository;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserPersistenceAdapter adapter;

    @Test
    void shouldFindUserByEmailSuccessfully() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("juan@test.com")
                .password("encoded")
                .status(true)
                .roleId(2L)
                .build();

        RoleEntity rolEntity = RoleEntity.builder()
                .id(2L)
                .name("PROPIETARIO")
                .description("Rol propietario")
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("juan@test.com")
                .password("encoded")
                .status(true)
                .role(Role.builder().id(2L).name("PROPIETARIO").description("Rol propietario").build())
                .build();

        when(userRepository.findByEmailAndStatusTrue(anyString())).thenReturn(Mono.just(userEntity));
        when(roleRepository.findById(anyLong())).thenReturn(Mono.just(rolEntity));
        when(userEntityMapper.toDomain(any(), any())).thenReturn(user);

        StepVerifier.create(adapter.findByEmail("juan@test.com"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void shouldReturnExistsByEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByEmail("test@test.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnExistsByNumberDocument() {
        when(userRepository.existsByNumberDocument(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNumberDocument("123456"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldFindRoleByNameSuccessfully() {
        RoleEntity rolEntity = RoleEntity.builder()
                .id(1L)
                .name("ADMINISTRADOR")
                .description("Administrador")
                .build();

        Role role = Role.builder()
                .id(1L)
                .name("ADMINISTRADOR")
                .description("Administrador")
                .build();

        when(roleRepository.findByName(anyString())).thenReturn(Mono.just(rolEntity));
        when(userEntityMapper.toDomain(any()))
                .thenReturn(role);

        StepVerifier.create(adapter.findRoleByName("ADMINISTRADOR"))
                .assertNext(roleFind -> {
                    Assertions.assertEquals(1L, roleFind.getId());
                    Assertions.assertEquals("ADMINISTRADOR", roleFind.getName());
                    Assertions.assertEquals("Administrador", roleFind.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        User user = User.builder()
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .birthDate(LocalDate.of(1992, Month.MAY, 10))
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .role(Role.builder()
                        .id(3L)
                        .name("EMPLEADO")
                        .description("Rol empleado")
                        .build())
                .build();

        UserEntity userEntity = UserEntity.builder()
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .birthDate(LocalDate.of(1992, Month.MAY, 10))
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .roleId(3L)
                .build();

        UserEntity savedEntity = UserEntity.builder()
                .id(10L)
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .birthDate(LocalDate.of(1992, Month.MAY, 10))
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .roleId(3L)
                .build();

        RoleEntity roleEntity = RoleEntity.builder()
                .id(3L)
                .description("Rol empleado")
                .name("EMPLEADO")
                .build();

        User expectedUser = User.builder()
                .id(10L)
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .birthDate(LocalDate.of(1992, Month.MAY, 10))
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .role(Role.builder()
                        .id(3L)
                        .name("EMPLEADO")
                        .description("Rol empleado")
                        .build())
                .build();

        when(userEntityMapper.toEntity(any(User.class)))
                .thenReturn(userEntity);

        when(userEntityMapper.toEntity(any(Role.class)))
                .thenReturn(roleEntity);

        when(userRepository.save(any()))
                .thenReturn(Mono.just(savedEntity));

        when(userEntityMapper.toDomain(any(), any()))
                .thenReturn(expectedUser);

        StepVerifier.create(adapter.save(user))
                .assertNext(savedUser -> {
                    Assertions.assertEquals(10L, savedUser.getId());
                    Assertions.assertEquals("Ana", savedUser.getFirstName());
                    Assertions.assertEquals("Lopez", savedUser.getLastName());
                    Assertions.assertEquals("789456", savedUser.getNumberDocument());
                    Assertions.assertEquals("+573009998877", savedUser.getPhone());
                    Assertions.assertEquals("ana@test.com", savedUser.getEmail());
                    Assertions.assertEquals("EMPLEADO", savedUser.getRole().getName());
                })
                .verifyComplete();
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("juan@test.com")
                .password("encoded")
                .status(true)
                .roleId(2L)
                .build();

        RoleEntity rolEntity = RoleEntity.builder()
                .id(2L)
                .name("PROPIETARIO")
                .description("Rol propietario")
                .build();

        User user = User.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 1))
                .email("juan@test.com")
                .password("encoded")
                .status(true)
                .role(Role.builder().id(2L).name("PROPIETARIO").description("Rol propietario").build())
                .build();

        when(userRepository.findByIdAndStatusTrue(anyLong())).thenReturn(Mono.just(userEntity));
        when(roleRepository.findById(anyLong())).thenReturn(Mono.just(rolEntity));
        when(userEntityMapper.toDomain(any(), any())).thenReturn(user);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(user)
                .verifyComplete();
    }


}
