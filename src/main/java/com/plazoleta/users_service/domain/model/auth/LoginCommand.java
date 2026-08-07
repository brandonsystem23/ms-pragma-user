package com.plazoleta.users_service.domain.model.auth;

public record LoginCommand(

        String correo,

        String password
) {
}
