package com.plazoleta.users_service.application.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(

        Long id,

        String firstName,

        String lastName,

        String numberDocument,

        String phone,

        LocalDate birthDate,

        String email,

        Boolean status,

        String role

) {
}
