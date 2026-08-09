package com.plazoleta.users_service.application.mapper;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(User user);

    @Mapping(target = "roleName", constant = RoleNames.OWNER)
    RegisterUserCommand toCommand(CreateOwnerRequest request);

    @Mapping(target = "roleName", constant = RoleNames.EMPLOYEE)
    @Mapping(target = "birthDate", ignore = true)
    RegisterUserCommand toCommand(CreateEmployeeRequest request);

    @Mapping(target = "roleName", constant = RoleNames.CLIENT)
    @Mapping(target = "birthDate", ignore = true)
    RegisterUserCommand toCommand(CreateClientRequest request);
}
