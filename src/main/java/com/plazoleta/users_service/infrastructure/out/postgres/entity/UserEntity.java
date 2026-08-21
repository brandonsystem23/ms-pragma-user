package com.plazoleta.users_service.infrastructure.out.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity {

    @Id
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("number_document")
    private String numberDocument;

    private String phone;

    @Column("birth_date")
    private LocalDate birthDate;

    private String email;

    private String password;

    private Boolean status;

    @Column("role_id")
    private Long roleId;

}
