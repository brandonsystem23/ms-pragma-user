package com.plazoleta.users_service.infrastructure.exceptionhandler;

import com.plazoleta.users_service.domain.exception.DomainErrorCode;
import com.plazoleta.users_service.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/test").build()
        );
    }

    @Test
    void shouldHandleValidationDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.VALIDATION_ERROR,
                                "El email ya está registrado"
                        ),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("El email ya está registrado", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleRoleNotFoundDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.ROLE_NOT_FOUND,
                                "ROLE_INEXISTENTE"
                        ),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not Found", response.error());
        assertEquals("ROLE_INEXISTENTE", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleInvalidCredentialsDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.INVALID_CREDENTIALS,
                                "Credenciales inválidas"
                        ),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Credenciales inválidas", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleAccessDenied() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleAccessDenied(new AccessDeniedException("Acceso denegado"), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("No tienes permisos para acceder a este recurso", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleIllegalArgument(new IllegalArgumentException("Authorization header inválido"), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Authorization header inválido", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleGenericException(new RuntimeException("Error inesperado"), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("Internal Server Error", response.error());
        assertEquals("Ocurrió un error interno en el servidor", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleValidationErrors() {
        WebExchangeBindException exception = mock(WebExchangeBindException.class);

        FieldError emailError = new FieldError(
                "testRequest",
                "email",
                "El email no tiene un formato válido"
        );

        FieldError passwordError = new FieldError(
                "testRequest",
                "password",
                "La contraseña es obligatoria"
        );

        when(exception.getFieldErrors())
                .thenReturn(List.of(emailError, passwordError));

        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleValidationErrors(exception, exchange);

        ErrorResponse body = getBody(responseEntity);

        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.status());
        assertEquals("Bad Request", body.error());
        assertEquals("Error de validación", body.message());
        assertEquals("/api/v1/test", body.path());

        assertEquals(
                List.of(
                        "email: El email no tiene un formato válido",
                        "password: La contraseña es obligatoria"
                ),
                body.details()
        );
    }

    @Test
    void shouldHandleAccessDeniedDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.ACCESS_DENIED,
                                "No tienes permisos para acceder a este recurso"
                        ),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("No tienes permisos para acceder a este recurso", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleInternalErrorDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.INTERNAL_ERROR,
                                "Ocurrió un error interno en el servidor"
                        ),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("Internal Server Error", response.error());
        assertEquals("Ocurrió un error interno en el servidor", response.message());
        assertEquals("/api/v1/test", response.path());
        assertEquals(List.of(), response.details());
    }

    private ErrorResponse getBody(ResponseEntity<ErrorResponse> responseEntity) {
        ErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        return body;
    }
}
