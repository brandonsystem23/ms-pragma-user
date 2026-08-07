package com.plazoleta.users_service.domain.usecase.auth;

import lombok.Builder;

@Builder
public record AuthResult(

        String token,

        String tokenType,

        Long userId,

        String role ) {
}