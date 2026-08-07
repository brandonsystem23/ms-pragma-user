package com.plazoleta.users_service.domain.exception;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String roleName) {
        super("El role " + roleName + " no existe");
    }
}
