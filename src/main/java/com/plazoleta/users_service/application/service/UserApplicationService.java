package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
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
        return registerUserUseCase.register(
                        new RegisterUserCommand(
                                request.nombre(),
                                request.apellido(),
                                request.documentoIdentidad(),
                                request.celular(),
                                request.fechaNacimiento(),
                                request.correo(),
                                request.password(),
                                RoleNames.PROPIETARIO
                        )
                )
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> createEmployee(CreateEmployeeRequest request) {
        return registerUserUseCase.register(
                        new RegisterUserCommand(
                                request.nombre(),
                                request.apellido(),
                                request.documentoIdentidad(),
                                request.celular(),
                                null,
                                request.correo(),
                                request.password(),
                                RoleNames.EMPLEADO
                        )
                )
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> selfRegisterClient(CreateClientRequest request) {
        return registerUserUseCase.register(
                        new RegisterUserCommand(
                                request.nombre(),
                                request.apellido(),
                                request.documentoIdentidad(),
                                request.celular(),
                                null,
                                request.correo(),
                                request.password(),
                                RoleNames.CLIENTE
                        )
                )
                .map(userDtoMapper::toResponse);
    }
}
