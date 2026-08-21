package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.CreateClientRequest;
import com.plazoleta.users_service.application.dto.request.CreateEmployeeRequest;
import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.dto.response.UserResponse;
import com.plazoleta.users_service.application.handler.IUserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UserController {

    private final IUserHandler iUserHandler;

    @PostMapping("/owners")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear propietario", description = "Crea un usuario con role propietario. Requiere rol ADMINISTRADOR")
    public Mono<UserResponse> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        return iUserHandler.createOwner(request);
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear empleado", description = "Crea un usuario con role empleado y lo asigna al restaurante. Requiere rol PROPIETARIO")
    public Mono<UserResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request,
                                             Authentication authentication) {
        Long ownerId = (Long) authentication.getPrincipal();
        return iUserHandler.createEmployee(request, ownerId);
    }

    @PostMapping("/clients/self-register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Auto registro de cliente", description = "Permite que un cliente se registre sin autenticación")
    public Mono<UserResponse> selfRegisterClient(@Valid @RequestBody CreateClientRequest request) {
        return iUserHandler.selfRegisterClient(request);
    }

    @GetMapping("/find")
    @Operation(summary = "Buscar usuario", description = "Busca un usuario por su id. Requiere rol ADMINISTRADOR")
    public Mono<UserResponse> retrieveUser(@RequestParam(value = "id") Long userId) {
        return iUserHandler.findUser(userId);
    }
}
