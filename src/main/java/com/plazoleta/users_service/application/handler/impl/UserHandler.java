package com.plazoleta.users_service.application.handler.impl;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.handler.IUserHandler;
import com.plazoleta.users_service.application.mapper.UserDtoMapper;
import com.plazoleta.users_service.domain.api.IUserRegisterServicePort;
import com.plazoleta.users_service.domain.api.IUserRetrieveServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserHandler implements IUserHandler {

    private final IUserRegisterServicePort iUserRegisterServicePort;
    private final IUserRetrieveServicePort iUserRetrieveServicePort;
    private final UserDtoMapper userDtoMapper;

    @Override
    public Mono<UserResponse> createOwner(CreateOwnerRequest request) {
        return iUserRegisterServicePort.register(userDtoMapper.toCommand(request), null)
                .map(userDtoMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> createEmployee(CreateEmployeeRequest request, Long ownerId) {
        return iUserRegisterServicePort.register(userDtoMapper.toCommand(request), ownerId)
                .map(userDtoMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> selfRegisterClient(CreateClientRequest request) {
        return iUserRegisterServicePort.register(userDtoMapper.toCommand(request), null)
                .map(userDtoMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> findUser(Long id) {
        return iUserRetrieveServicePort.find(id)
                .map(userDtoMapper::toResponse);
    }
}
