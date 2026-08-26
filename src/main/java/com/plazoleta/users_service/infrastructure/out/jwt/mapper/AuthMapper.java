package com.plazoleta.users_service.infrastructure.out.jwt.mapper;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.infrastructure.out.jwt.dto.AuthenticatedUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthenticatedUser toDto(AuthSession authSession);
}
