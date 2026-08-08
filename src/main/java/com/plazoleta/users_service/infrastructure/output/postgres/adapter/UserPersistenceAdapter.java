package com.plazoleta.users_service.infrastructure.output.postgres.adapter;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.port.out.UserPersistencePort;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RoleEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.UserEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.mapper.UserEntityMapper;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.RoleRepository;
import com.plazoleta.users_service.infrastructure.output.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository userRepository;
    private final RoleRepository rolRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(this::mapUserWithRole);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByNumberDocument(String numberDocument) {
        return userRepository.existsByNumberDocument(numberDocument);
    }

    @Override
    public Mono<Role> findRoleByName(String name) {
        return rolRepository.findByName(name)
                .map(this::mapRolToDomain);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);

        return userRepository.save(userEntity)
                .map(savedEntity -> User.builder()
                        .id(savedEntity.getId())
                        .firstName(savedEntity.getFirstName())
                        .lastName(savedEntity.getLastName())
                        .numberDocument(savedEntity.getNumberDocument())
                        .phone(savedEntity.getPhone())
                        .birthDate(savedEntity.getBirthDate())
                        .email(savedEntity.getEmail())
                        .password(savedEntity.getPassword())
                        .status(savedEntity.getStatus())
                        .role(user.getRole())
                        .build());
    }

    private Mono<User> mapUserWithRole(UserEntity userEntity) {
        return rolRepository.findById(userEntity.getRoleId())
                .map(rolEntity -> userEntityMapper.toDomain(userEntity, rolEntity));
    }

    private Role mapRolToDomain(RoleEntity rolEntity) {
        return Role.builder()
                .id(rolEntity.getId())
                .name(rolEntity.getName())
                .description(rolEntity.getDescription())
                .build();
    }
}
