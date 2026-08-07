package com.plazoleta.users_service.application.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(

        String token,

        String tokenType,

        Long userId,

        String role

) {
}
