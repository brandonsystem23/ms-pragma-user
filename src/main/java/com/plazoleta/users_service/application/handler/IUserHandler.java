package com.plazoleta.users_service.application.handler;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import reactor.core.publisher.Mono;

public interface IUserHandler {

    Mono<UserResponse> createOwner(CreateOwnerRequest request);

    Mono<UserResponse> createEmployee(CreateEmployeeRequest request, Long ownerId);

    Mono<UserResponse> selfRegisterClient(CreateClientRequest request);

    Mono<UserResponse> findUser(Long id);
}
