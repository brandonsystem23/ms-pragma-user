package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.mapper.ClientDtoMapper;
import com.plazoleta.users_service.application.mapper.EmployeeDtoMapper;
import com.plazoleta.users_service.application.mapper.OwnerDtoMapper;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserDtoMapper userDtoMapper;
    private final OwnerDtoMapper ownerDtoMapper;
    private final EmployeeDtoMapper employeeDtoMapper;
    private final ClientDtoMapper clientDtoMapper;

    public Mono<UserResponse> createOwner(CreateOwnerRequest request) {
        return registerUserUseCase.register(ownerDtoMapper.toCommand(request))
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> createEmployee(CreateEmployeeRequest request) {
        return registerUserUseCase.register(employeeDtoMapper.toCommand(request))
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> selfRegisterClient(CreateClientRequest request) {
        return registerUserUseCase.register(clientDtoMapper.toCommand(request))
                .map(userDtoMapper::toResponse);
    }
}
