package com.plazoleta.users_service.domain.exception;

public enum DomainErrorCode {
    VALIDATION_ERROR,
    DUPLICATE_DOCUMENT,
    DUPLICATE_EMAIL,
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    ROLE_NOT_FOUND,
    ACCESS_DENIED,
    RESTAURANT_NOT_FOUND,
    INTERNAL_ERROR
}
