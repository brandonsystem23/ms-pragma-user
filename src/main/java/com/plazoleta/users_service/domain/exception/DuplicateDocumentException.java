package com.plazoleta.users_service.domain.exception;

public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException() {
        super("El numberDocument de identidad ya está registrado");
    }
}
