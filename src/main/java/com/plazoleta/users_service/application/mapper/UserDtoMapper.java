package com.plazoleta.users_service.application.mapper;

import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(User user);
}
