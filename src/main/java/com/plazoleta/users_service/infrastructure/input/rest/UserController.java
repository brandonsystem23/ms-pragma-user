package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.service.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/owners")
    @Operation(summary = "Crear propietario", description = "Crea un usuario con role propietario. Requiere rol ADMIN")
    public Mono<UserResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        return userApplicationService.createOwner(request);
    }

    @PostMapping("/employees")
    @Operation(summary = "Crear empleado", description = "Crea un usuario con role empleado. Requiere rol PROPIETARIO")
    public Mono<UserResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return userApplicationService.createEmployee(request);
    }

    @PostMapping("/clients/self-register")
    @Operation(summary = "Auto registro de cliente", description = "Permite que un cliente se registre sin autenticación")
    public Mono<UserResponse> selfRegisterClient(@Valid @RequestBody CreateClientRequest request) {
        return userApplicationService.selfRegisterClient(request);
    }
}
