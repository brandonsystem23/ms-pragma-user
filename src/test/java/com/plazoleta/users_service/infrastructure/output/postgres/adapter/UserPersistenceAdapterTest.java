package com.plazoleta.users_service.infrastructure.output.postgres.adapter;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RoleEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.UserEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.mapper.UserEntityMapper;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.RoleRepository;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class UserPersistenceAdapterTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserEntityMapper userEntityMapper;
    private UserPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        userEntityMapper = mock(UserEntityMapper.class);
        adapter = new UserPersistenceAdapter(userRepository, roleRepository, userEntityMapper);
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .numberDocument("123456")
                .phone("+573001112233")
                .birthDate(LocalDate.of(1990, 1, 1))
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
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("juan@test.com")
                .password("encoded")
                .status(true)
                .role(Role.builder().id(2L).name("PROPIETARIO").description("Rol propietario").build())
                .build();

        when(userRepository.findByEmailAndStatusTrue("juan@test.com")).thenReturn(Mono.just(userEntity));
        when(roleRepository.findById(2L)).thenReturn(Mono.just(rolEntity));
        when(userEntityMapper.toDomain(userEntity, rolEntity)).thenReturn(user);

        StepVerifier.create(adapter.findByEmail("juan@test.com"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void shouldReturnExistsByEmail() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByEmail("test@test.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnExistsByNumberDocument() {
        when(userRepository.existsByNumberDocument("123456")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNumberDocument("123456"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldFindRoleByNameSuccessfully() {
        RoleEntity rolEntity = RoleEntity.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrador")
                .build();

        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.just(rolEntity));

        StepVerifier.create(adapter.findRoleByName("ADMIN"))
                .assertNext(role -> {
                    assert 1L == role.getId();
                    assert "ADMIN".equals(role.getName());
                    assert "Administrador".equals(role.getDescription());
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
                .birthDate(LocalDate.of(1992, 5, 10))
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .role(Role.builder().id(3L).name("EMPLEADO").description("Rol empleado").build())
                .build();

        UserEntity userEntity = UserEntity.builder()
                .firstName("Ana")
                .lastName("Lopez")
                .numberDocument("789456")
                .phone("+573009998877")
                .birthDate(LocalDate.of(1992, 5, 10))
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
                .birthDate(LocalDate.of(1992, 5, 10))
                .email("ana@test.com")
                .password("encoded-password")
                .status(true)
                .roleId(3L)
                .build();

        when(userEntityMapper.toEntity(user)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(Mono.just(savedEntity));

        StepVerifier.create(adapter.save(user))
                .assertNext(savedUser -> {
                    assert 10L == savedUser.getId();
                    assert "Ana".equals(savedUser.getFirstName());
                    assert "Lopez".equals(savedUser.getLastName());
                    assert "789456".equals(savedUser.getNumberDocument());
                    assert "+573009998877".equals(savedUser.getPhone());
                    assert "ana@test.com".equals(savedUser.getEmail());
                    assert "EMPLEADO".equals(savedUser.getRole().getName());
                })
                .verifyComplete();
    }
}
