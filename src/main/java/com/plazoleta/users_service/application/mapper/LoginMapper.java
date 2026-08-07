package com.plazoleta.users_service.application.mapper;

import com.plazoleta.users_service.application.dto.request.LoginRequest;
import com.plazoleta.users_service.application.dto.response.LoginResponse;
import com.plazoleta.users_service.domain.usecase.auth.AuthResult;
import com.plazoleta.users_service.domain.usecase.auth.LoginCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {

    LoginCommand toCommand(LoginRequest request);

    LoginResponse toResponse(AuthResult authResult);

}
