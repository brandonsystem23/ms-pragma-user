package com.plazoleta.users_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String firstName;

    private String lastName;

    private String numberDocument;

    private String phone;

    private LocalDate birthDate;

    private String email;

    private String password;

    private Boolean status;

    private Role role;

}
