package com.plazoleta.users_service.application.dto.request;

import com.plazoleta.users_service.application.validation.Adult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateOwnerRequest(

        @NotBlank(message = "El firstName es obligatorio")
        String firstName,

        @NotBlank(message = "El lastName es obligatorio")
        String lastName,

        @NotBlank(message = "El numberDocument de identidad es obligatorio")
        @Pattern(
                regexp = "\\d+",
                message = "El numberDocument de identidad debe contener únicamente números"
        )
        String numberDocument,

        @NotBlank(message = "El phone es obligatorio")
        @Size(
                max = 13,
                message = "El phone no puede tener más de 13 caracteres"
        )
        @Pattern(
                regexp = "^\\+?\\d+$",
                message = "El phone solo puede contener números y opcionalmente iniciar con +"
        )
        String phone,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Adult(message = "El usuario debe tener 18 años o más")
        LocalDate birthDate,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
