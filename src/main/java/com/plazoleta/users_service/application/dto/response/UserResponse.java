package com.plazoleta.users_service.application.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(

        Long id,

        String nombre,

        String apellido,

        String documentoIdentidad,

        String telefono,

        LocalDate fechaNacimiento,

        String correo,

        Boolean activo,

        String rol

) {
}
