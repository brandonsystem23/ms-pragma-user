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
    private final RoleRepository roleRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmailAndStatusTrue(email)
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
        return roleRepository.findByName(name)
                .map(this::toDomainRole);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);

        return userRepository.save(userEntity)
                .map(savedEntity -> toSavedDomain(user, savedEntity));
    }

    private Mono<User> mapUserWithRole(UserEntity userEntity) {
        return roleRepository.findById(userEntity.getRoleId())
                .map(roleEntity -> userEntityMapper.toDomain(userEntity, roleEntity));
    }

    private Role toDomainRole(RoleEntity roleEntity) {
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .build();
    }

    private User toSavedDomain(User originalUser, UserEntity savedEntity) {
        return User.builder()
                .id(savedEntity.getId())
                .firstName(savedEntity.getFirstName())
                .lastName(savedEntity.getLastName())
                .numberDocument(savedEntity.getNumberDocument())
                .phone(savedEntity.getPhone())
                .birthDate(savedEntity.getBirthDate())
                .email(savedEntity.getEmail())
                .password(savedEntity.getPassword())
                .status(savedEntity.getStatus())
                .role(originalUser.getRole())
                .build();
    }
}
