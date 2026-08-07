package com.plazoleta.users_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "El documento es obligatorio")
        String documentoIdentidad,

        @NotBlank(message = "La contraseña es obligatoria")
        String password

) {
}
