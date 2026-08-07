package com.plazoleta.users_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    private Long id;

    private String nombre;

    private String descripcion;

}
