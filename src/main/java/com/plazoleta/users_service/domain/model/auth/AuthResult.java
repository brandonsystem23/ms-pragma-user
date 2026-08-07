package com.plazoleta.users_service.domain.model.auth;

import lombok.Builder;

@Builder
public record AuthResult(

        String token,
        String tokenType,
        Long userId,
        String role
) {
}