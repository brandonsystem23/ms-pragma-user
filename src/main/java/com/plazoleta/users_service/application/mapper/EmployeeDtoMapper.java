package com.plazoleta.users_service.application.mapper;

import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeDtoMapper {
    @Mapping(target = "roleName", constant = RoleNames.EMPLOYEE)
    @Mapping(target = "birthDate", ignore = true)
    RegisterUserCommand toCommand(CreateEmployeeRequest request);
}
