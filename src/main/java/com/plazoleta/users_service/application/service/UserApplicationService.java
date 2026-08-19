package com.plazoleta.users_service.application.service;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.port.in.RegisterUserUseCase;
import com.plazoleta.users_service.domain.port.in.RetrieveUserCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final RegisterUserUseCase registerUserUseCase;
    private final RetrieveUserCase retrieveUserCase;
    private final UserDtoMapper userDtoMapper;

    public Mono<UserResponse> createOwner(CreateOwnerRequest request) {
        return registerUserUseCase.register(userDtoMapper.toCommand(request), null)
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> createEmployee(CreateEmployeeRequest request, Long ownerId) {
        return registerUserUseCase.register(userDtoMapper.toCommand(request), ownerId)
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> selfRegisterClient(CreateClientRequest request) {
        return registerUserUseCase.register(userDtoMapper.toCommand(request), null)
                .map(userDtoMapper::toResponse);
    }

    public Mono<UserResponse> findUser(Long id) {
        return retrieveUserCase.find(id)
                .map(userDtoMapper::toResponse);
    }
}
