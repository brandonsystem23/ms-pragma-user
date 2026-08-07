package com.plazoleta.users_service.domain.model.auth;

import java.time.LocalDate;

public record RegisterUserCommand(

        String firstName,
        String lastName,
        String numberDocument,
        String phone,
        LocalDate birthDate,
        String email,
        String password,
        String roleName
) {
}
