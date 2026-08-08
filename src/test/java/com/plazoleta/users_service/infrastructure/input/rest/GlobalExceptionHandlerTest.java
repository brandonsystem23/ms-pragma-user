package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void shouldHandleUserNotFound() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleUserNotFound(new UserNotFoundException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Usuario no encontrado", response.message());
        assertEquals("/api/v1/test", response.path());
    }

    @Test
    void shouldHandleInvalidCredentials() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleInvalidCredentials(new InvalidCredentialsException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Credenciales inválidas", response.message());
    }

    @Test
    void shouldHandleBadCredentials() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleInvalidCredentials(
                        new BadCredentialsException("Credenciales incorrectas"),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Credenciales incorrectas", response.message());
    }

    @Test
    void shouldHandleDuplicateEmail() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDuplicateData(new DuplicateEmailException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.CONFLICT.value(), response.status());
        assertEquals("Conflict", response.error());
        assertEquals("El email ya está registrado", response.message());
    }

    @Test
    void shouldHandleDuplicateDocument() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleDuplicateData(new DuplicateDocumentException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.CONFLICT.value(), response.status());
        assertEquals("Conflict", response.error());
        assertEquals("El numberDocument de identidad ya está registrado", response.message());
    }

    @Test
    void shouldHandleRoleNotFound() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleRoleNotFound(new RoleNotFoundException("ADMIN"), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not Found", response.error());
        assertEquals("El role ADMIN no existe", response.message());
    }

    @Test
    void shouldHandleAccessDenied() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleAccessDenied(
                        new AccessDeniedException("Acceso denegado"),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("No tienes permisos para acceder a este recurso", response.message());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleIllegalArgument(
                        new IllegalArgumentException("Authorization header inválido"),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Authorization header inválido", response.message());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleGenericException(
                        new RuntimeException("Error inesperado"),
                        exchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("Internal Server Error", response.error());
        assertEquals("Ocurrió un error interno en el servidor", response.message());
    }

    @Test
    void shouldHandleValidationErrors() throws Exception {
        Object target = new TestRequest();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testRequest");
        bindingResult.addError(new FieldError("testRequest", "email", "El email no tiene un formato válido"));
        bindingResult.addError(new FieldError("testRequest", "password", "La contraseña es obligatoria"));

        Method method = TestController.class.getDeclaredMethod("testMethod", TestRequest.class);

        WebExchangeBindException exception = new WebExchangeBindException(
                new org.springframework.core.MethodParameter(method, 0),
                bindingResult
        );

        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleValidationErrors(exception, exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Error de validación", response.message());
        assertEquals(2, response.details().size());
        assertTrue(response.details().contains("email: El email no tiene un formato válido"));
        assertTrue(response.details().contains("password: La contraseña es obligatoria"));
    }

    private ErrorResponse getBody(ResponseEntity<ErrorResponse> responseEntity) {
        ErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        return body;
    }

    static class TestController {
        public void testMethod(@ModelAttribute TestRequest request) {
        }
    }

    static class TestRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
