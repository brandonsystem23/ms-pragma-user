package com.plazoleta.users_service.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(

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

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
