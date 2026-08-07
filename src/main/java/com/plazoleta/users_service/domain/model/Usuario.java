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
public class Usuario {

    private Long id;

    private String nombre;

    private String apellido;

    private String documentoIdentidad;

    private String telefono;

    private LocalDate fechaNacimiento;

    private String correo;

    private String password;

    private Boolean activo;

    private Rol rol;

}
