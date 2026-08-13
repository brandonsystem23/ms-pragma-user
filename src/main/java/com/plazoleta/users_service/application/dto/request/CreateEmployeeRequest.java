package com.plazoleta.users_service.application.dto.request;


public record CreateEmployeeRequest(

        String firstName,

        String lastName,

        String numberDocument,

        String phone,

        String email,

        String password
) {
}
