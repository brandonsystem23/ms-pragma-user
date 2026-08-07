package com.plazoleta.users_service.domain.model.auth;

import lombok.Builder;

@Builder
public record AuthSession(

        Long userId,
        String role,
        String documento,
        String telefono,
        String correo
) {
}
