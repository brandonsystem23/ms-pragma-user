package com.plazoleta.users_service.infrastructure.output.postgres.mapper;

import com.plazoleta.users_service.domain.model.Rol;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RolEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolEntityMapper {

    Rol toDomain(RolEntity entity);

    RolEntity toEntity(Rol rol);

}
