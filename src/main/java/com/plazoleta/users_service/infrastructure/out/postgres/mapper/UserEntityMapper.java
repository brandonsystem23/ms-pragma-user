package com.plazoleta.users_service.infrastructure.out.postgres.mapper;

import com.plazoleta.users_service.domain.model.Role;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.infrastructure.out.postgres.entity.RoleEntity;
import com.plazoleta.users_service.infrastructure.out.postgres.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "numberDocument", source = "user.numberDocument")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "birthDate", source = "user.birthDate")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "status", source = "user.status")

    @Mapping(target = "role.id", source = "role.id")
    @Mapping(target = "role.name", source = "role.name")
    @Mapping(target = "role.description", source = "role.description")
    User toDomain(UserEntity user, RoleEntity role);

    @Mapping(target = "roleId", source = "role.id")
    UserEntity toEntity(User user);

    Role toDomain(RoleEntity roleEntity);

    RoleEntity toEntity(Role role);
}