package com.plazoleta.users_service.domain.exception;

public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException() {
        super("El documento de identidad ya está registrado");
    }
}
