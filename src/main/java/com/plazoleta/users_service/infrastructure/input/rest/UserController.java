package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.service.UserApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/owners")
    public Mono<UserResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        return userApplicationService.createOwner(request);
    }

    @PostMapping("/employees")
    public Mono<UserResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return userApplicationService.createEmployee(request);
    }

    @PostMapping("/clients/self-register")
    public Mono<UserResponse> selfRegisterClient(@Valid @RequestBody CreateClientRequest request) {
        return userApplicationService.selfRegisterClient(request);
    }
}
