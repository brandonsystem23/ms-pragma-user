package com.plazoleta.users_service.infrastructure.output.postgres.mapper;

import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.RolEntity;
import com.plazoleta.users_service.infrastructure.output.postgres.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioEntityMapper {

    @Mapping(target = "id", source = "usuario.id")
    @Mapping(target = "nombre", source = "usuario.nombre")
    @Mapping(target = "apellido", source = "usuario.apellido")
    @Mapping(target = "documentoIdentidad", source = "usuario.documentoIdentidad")
    @Mapping(target = "telefono", source = "usuario.telefono")
    @Mapping(target = "fechaNacimiento", source = "usuario.fechaNacimiento")
    @Mapping(target = "correo", source = "usuario.correo")
    @Mapping(target = "password", source = "usuario.password")
    @Mapping(target = "activo", source = "usuario.activo")

    @Mapping(target = "rol.id", source = "rol.id")
    @Mapping(target = "rol.nombre", source = "rol.nombre")
    @Mapping(target = "rol.descripcion", source = "rol.descripcion")
    Usuario toDomain(UsuarioEntity usuario, RolEntity rol);

    @Mapping(target = "rolId", source = "rol.id")
    UsuarioEntity toEntity(Usuario usuario);
}