package com.plazoleta.users_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record LoginRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password

) {
}
