package com.plazoleta.users_service.domain.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("El email ya está registrado");
    }
}
