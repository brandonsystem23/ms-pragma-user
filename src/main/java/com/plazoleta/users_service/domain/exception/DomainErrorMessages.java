package com.plazoleta.users_service.domain.exception;

public final class DomainErrorMessages {

    public static final String FIRST_NAME_REQUIRED = "El campo firstName es obligatorio";
    public static final String LAST_NAME_REQUIRED = "El campo lastName es obligatorio";
    public static final String DOCUMENT_REQUIRED = "El campo numberDocument es obligatorio";
    public static final String DOCUMENT_NUMERIC = "El campo numberDocument debe contener únicamente números";
    public static final String PHONE_REQUIRED = "El campo phone es obligatorio";
    public static final String PHONE_MAX_LENGTH = "El phone no puede tener más de 13 caracteres";
    public static final String PHONE_INVALID = "El phone solo puede contener números y opcionalmente iniciar con +";
    public static final String BIRTH_DATE_REQUIRED = "El campo birthDate es obligatorio";
    public static final String USER_MUST_BE_ADULT = "El usuario debe tener 18 años o más";
    public static final String EMAIL_REQUIRED = "El campo email es obligatorio";
    public static final String EMAIL_INVALID = "El email no tiene un formato válido";
    public static final String PASS_REQUIRED = "La contraseña es obligatoria";

    public static final String DUPLICATE_DOCUMENT = "El numero de documento  ya está registrado";
    public static final String DUPLICATE_EMAIL = "El email ya está registrado";
    public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String INVALID_RESTAURANT = "No se encontró un restaurante asociado al propietario";

    private DomainErrorMessages() {
    }
}
