package com.plazoleta.users_service.domain.usecase.auth;

public record LoginCommand(

        String documentoIdentidad,

        String password ) {
}
