package com.plazoleta.users_service.infrastructure.output.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("roles")
public class RolEntity {

    @Id
    private Long id;

    private String nombre;

    private String descripcion;

}
