package com.plazoleta.users_service.domain.model.auth;

import java.time.LocalDate;

public record RegisterUserCommand(

        String nombre,
        String apellido,
        String documentoIdentidad,
        String telefono,
        LocalDate fechaNacimiento,
        String correo,
        String password,
        String roleName
) {
}
