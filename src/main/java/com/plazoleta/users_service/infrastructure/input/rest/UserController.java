package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.application.dto.request.CreateOwnerRequest;
import com.plazoleta.users_service.application.handler.CreateOwnerHandler;
import com.plazoleta.users_service.domain.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final CreateOwnerHandler createOwnerHandler;


    @PostMapping("/owners")
    public Mono<Usuario> createOwner(
            @Valid @RequestBody CreateOwnerRequest request
    ) {

        return createOwnerHandler.createOwner(request);
    }
}
