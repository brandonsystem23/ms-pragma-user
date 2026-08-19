package com.plazoleta.users_service.application.dto.request;

public record LoginRequest(

        String email,

        String password

) {
}
