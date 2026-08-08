package com.plazoleta.users_service.application.mapper;

import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OwnerDtoMapper {
    @Mapping(target = "roleName", constant = RoleNames.OWNER)
    RegisterUserCommand toCommand(CreateOwnerRequest request);
}
