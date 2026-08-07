package com.plazoleta.users_service.application.dto.request;

import com.plazoleta.users_service.infrastructure.validation.Adult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateOwnerRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El documento de identidad es obligatorio")
        @Pattern(
                regexp = "\\d+",
                message = "El documento de identidad debe contener únicamente números"
        )
        String documentoIdentidad,

        @NotBlank(message = "El celular es obligatorio")
        @Size(
                max = 13,
                message = "El celular no puede tener más de 13 caracteres"
        )
        @Pattern(
                regexp = "^\\+?\\d+$",
                message = "El celular solo puede contener números y opcionalmente iniciar con +"
        )
        String celular,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Adult(message = "El usuario debe tener 18 años o más")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
