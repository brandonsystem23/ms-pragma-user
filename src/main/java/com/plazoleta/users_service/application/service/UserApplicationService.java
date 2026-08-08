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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserDtoMapper userDtoMapper;

    public Mono<UserResponse> createOwner(CreateOwnerRequest request) {
        return register(buildCommand(
                request.firstName(),
                request.lastName(),
                request.numberDocument(),
                request.phone(),
                request.birthDate(),
                request.email(),
                request.password(),
                RoleNames.OWNER
        ));
    }

    public Mono<UserResponse> createEmployee(CreateEmployeeRequest request) {
        return register(buildCommand(
                request.firstName(),
                request.lastName(),
                request.numberDocument(),
                request.phone(),
                null,
                request.email(),
                request.password(),
                RoleNames.EMPLOYEE
        ));
    }

    public Mono<UserResponse> selfRegisterClient(CreateClientRequest request) {
        return register(buildCommand(
                request.firstName(),
                request.lastName(),
                request.numberDocument(),
                request.phone(),
                null,
                request.email(),
                request.password(),
                RoleNames.CLIENT
        ));
    }

    private Mono<UserResponse> register(RegisterUserCommand command) {
        return registerUserUseCase.register(command)
                .map(userDtoMapper::toResponse);
    }

    private RegisterUserCommand buildCommand(
            String firstName,
            String lastName,
            String numberDocument,
            String phone,
            LocalDate birthDate,
            String email,
            String password,
            String roleName
    ) {
        return new RegisterUserCommand(
                firstName,
                lastName,
                numberDocument,
                phone,
                birthDate,
                email,
                password,
                roleName
        );
    }
}
