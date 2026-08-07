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

    private UserRepository usuarioRepository;
    private RoleRepository rolRepository;
    private UserEntityMapper usuarioEntityMapper;
    private UserPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UserRepository.class);
        rolRepository = mock(RoleRepository.class);
        usuarioEntityMapper = mock(UserEntityMapper.class);
        adapter = new UserPersistenceAdapter(usuarioRepository, rolRepository, usuarioEntityMapper);
    }

    @Test
    void shouldFindUserByCorreoSuccessfully() {
        UserEntity usuarioEntity = UserEntity.builder()
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

        User usuario = User.builder()
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

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Mono.just(usuarioEntity));
        when(rolRepository.findById(2L)).thenReturn(Mono.just(rolEntity));
        when(usuarioEntityMapper.toDomain(usuarioEntity, rolEntity)).thenReturn(usuario);

        StepVerifier.create(adapter.findByEmail("juan@test.com"))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void shouldReturnExistsByCorreo() {
        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByEmail("test@test.com"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnExistsByDocumento() {
        when(usuarioRepository.existsByNumberDocument("123456")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNumberDocument("123456"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldFindRoleByNombreSuccessfully() {
        RoleEntity rolEntity = RoleEntity.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrador")
                .build();

        when(rolRepository.findByName("ADMIN")).thenReturn(Mono.just(rolEntity));

        StepVerifier.create(adapter.findRoleByFirstName("ADMIN"))
                .assertNext(rol -> {
                    assert 1L == rol.getId();
                    assert "ADMIN".equals(rol.getName());
                    assert "Administrador".equals(rol.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        User usuario = User.builder()
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

        UserEntity usuarioEntity = UserEntity.builder()
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

        when(usuarioEntityMapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(usuarioRepository.save(usuarioEntity)).thenReturn(Mono.just(savedEntity));

        StepVerifier.create(adapter.save(usuario))
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
