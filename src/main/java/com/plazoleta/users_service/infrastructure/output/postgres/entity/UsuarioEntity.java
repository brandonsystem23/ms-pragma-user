package com.plazoleta.users_service.infrastructure.output.postgres.entity;

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
@Table("usuarios")
public class UsuarioEntity {

    @Id
    private Long id;

    private String nombre;

    private String apellido;

    @Column("documento_identidad")
    private String documentoIdentidad;

    private String telefono;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String correo;

    private String password;

    private Boolean activo;

    @Column("rol_id")
    private Long rolId;

}
