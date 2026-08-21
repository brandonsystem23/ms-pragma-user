package com.plazoleta.users_service.infrastructure.out.postgres.adapter;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.spi.IUserPersistencePort;
import com.plazoleta.users_service.infrastructure.out.postgres.entity.RoleEntity;
import com.plazoleta.users_service.infrastructure.out.postgres.entity.UserEntity;
import com.plazoleta.users_service.infrastructure.out.postgres.mapper.UserEntityMapper;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IRoleRepository;
import com.plazoleta.users_service.infrastructure.out.postgres.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements IUserPersistencePort {

    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Mono<User> findByEmail(String email) {
        return iUserRepository.findByEmailAndStatusTrue(email)
                .flatMap(this::mapUserWithRole);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return iUserRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByNumberDocument(String numberDocument) {
        return iUserRepository.existsByNumberDocument(numberDocument);
    }

    @Override
    public Mono<Role> findRoleByName(String name) {
        return iRoleRepository.findByName(name)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        RoleEntity roleEntity = userEntityMapper.toEntity(user.getRole());
        return iUserRepository.save(userEntity)
                .map(savedEntity -> userEntityMapper.toDomain(savedEntity, roleEntity));
    }

    @Override
    public Mono<User> findById(Long id) {
        return iUserRepository.findByIdAndStatusTrue(id)
                .flatMap(this::mapUserWithRole);
    }

    private Mono<User> mapUserWithRole(UserEntity userEntity) {
        return iRoleRepository.findById(userEntity.getRoleId())
                .map(roleEntity -> userEntityMapper.toDomain(userEntity, roleEntity));
    }

}
