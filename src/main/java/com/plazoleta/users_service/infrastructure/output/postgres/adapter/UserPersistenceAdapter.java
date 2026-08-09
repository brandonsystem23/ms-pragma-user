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
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        RoleEntity roleEntity = userEntityMapper.toEntity(user.getRole());
        return userRepository.save(userEntity)
                .map(savedEntity -> userEntityMapper.toDomain(savedEntity, roleEntity));
    }

    private Mono<User> mapUserWithRole(UserEntity userEntity) {
        return roleRepository.findById(userEntity.getRoleId())
                .map(roleEntity -> userEntityMapper.toDomain(userEntity, roleEntity));
    }

}
