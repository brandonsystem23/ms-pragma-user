package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.model.auth.RegisterUserCommand;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserDtoMapper userDtoMapper;

    public Mono<UserResponse> createOwner(CreateOwnerRequest request) {
        return registerUserUseCase.register(toOwnerCommand(request))
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> createEmployee(CreateEmployeeRequest request) {
        return registerUserUseCase.register(toEmployeeCommand(request))
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> selfRegisterClient(CreateClientRequest request) {
        return registerUserUseCase.register(toClientCommand(request))
                .map(userDtoMapper::toResponse);
    }

    private RegisterUserCommand toOwnerCommand(CreateOwnerRequest request) {
        return new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.numberDocument(),
                request.phone(),
                request.birthDate(),
                request.email(),
                request.password(),
                RoleNames.OWNER
        );
    }

    private RegisterUserCommand toEmployeeCommand(CreateEmployeeRequest request) {
        return new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.numberDocument(),
                request.phone(),
                null,
                request.email(),
                request.password(),
                RoleNames.EMPLOYEE
        );
    }

    private RegisterUserCommand toClientCommand(CreateClientRequest request) {
        return new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.numberDocument(),
                request.phone(),
                null,
                request.email(),
                request.password(),
                RoleNames.CLIENT
        );
    }
}
