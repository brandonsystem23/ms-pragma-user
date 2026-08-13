package com.plazoleta.users_service.domain.exception;

public final class DomainErrorMessages {

    public static final String FIRST_NAME_REQUIRED = "El firstName es obligatorio";
    public static final String LAST_NAME_REQUIRED = "El lastName es obligatorio";
    public static final String DOCUMENT_REQUIRED = "El numberDocument de identidad es obligatorio";
    public static final String DOCUMENT_NUMERIC = "El numberDocument de identidad debe contener únicamente números";
    public static final String PHONE_REQUIRED = "El phone es obligatorio";
    public static final String PHONE_MAX_LENGTH = "El phone no puede tener más de 13 caracteres";
    public static final String PHONE_INVALID = "El phone solo puede contener números y opcionalmente iniciar con +";
    public static final String BIRTH_DATE_REQUIRED = "La fecha de nacimiento es obligatoria";
    public static final String USER_MUST_BE_ADULT = "El usuario debe tener 18 años o más";
    public static final String EMAIL_REQUIRED = "El email es obligatorio";
    public static final String EMAIL_INVALID = "El email no tiene un formato válido";
    public static final String PASS_REQUIRED = "La contraseña es obligatoria";

    public static final String DUPLICATE_DOCUMENT = "El numberDocument de identidad ya está registrado";
    public static final String DUPLICATE_EMAIL = "El email ya está registrado";
    public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String USER_NOT_FOUND = "Usuario no encontrado";

    private DomainErrorMessages() {
    }
}
