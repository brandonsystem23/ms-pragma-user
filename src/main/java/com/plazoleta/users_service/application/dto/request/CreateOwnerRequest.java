package com.plazoleta.users_service.application.dto.request;



import java.time.LocalDate;

public record CreateOwnerRequest(

        String firstName,

        String lastName,

        String numberDocument,

        String phone,

        LocalDate birthDate,

        String email,

        String password
) {
}
