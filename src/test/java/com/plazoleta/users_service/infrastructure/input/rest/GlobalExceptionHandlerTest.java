package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        ErrorResponse response = handler.handleUserNotFound(new UserNotFoundException(), exchange);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Usuario no encontrado", response.message());
        assertEquals("/api/v1/test", response.path());
    }

    @Test
    void shouldHandleInvalidCredentials() {
        ErrorResponse response = handler.handleInvalidCredentials(new InvalidCredentialsException(), exchange);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Credenciales inválidas", response.message());
    }

    @Test
    void shouldHandleBadCredentials() {
        ErrorResponse response = handler.handleInvalidCredentials(
                new BadCredentialsException("Credenciales incorrectas"),
                exchange
        );

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Credenciales incorrectas", response.message());
    }

    @Test
    void shouldHandleDuplicateEmail() {
        ErrorResponse response = handler.handleDuplicateData(new DuplicateEmailException(), exchange);

        assertEquals(HttpStatus.CONFLICT.value(), response.status());
        assertEquals("Conflict", response.error());
        assertEquals("El email ya está registrado", response.message());
    }

    @Test
    void shouldHandleDuplicateDocument() {
        ErrorResponse response = handler.handleDuplicateData(new DuplicateDocumentException(), exchange);

        assertEquals(HttpStatus.CONFLICT.value(), response.status());
        assertEquals("Conflict", response.error());
        assertEquals("El numberDocument de identidad ya está registrado", response.message());
    }

    @Test
    void shouldHandleRoleNotFound() {
        ErrorResponse response = handler.handleRoleNotFound(
                new RoleNotFoundException("ADMIN"),
                exchange
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not Found", response.error());
        assertEquals("El role ADMIN no existe", response.message());
    }

    @Test
    void shouldHandleAccessDenied() {
        ErrorResponse response = handler.handleAccessDenied(
                new AccessDeniedException("Acceso denegado"),
                exchange
        );

        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("No tienes permisos para acceder a este recurso", response.message());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ErrorResponse response = handler.handleIllegalArgument(
                new IllegalArgumentException("Authorization header inválido"),
                exchange
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Authorization header inválido", response.message());
    }

    @Test
    void shouldHandleGenericException() {
        ErrorResponse response = handler.handleGenericException(
                new RuntimeException("Error inesperado"),
                exchange
        );

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

        ErrorResponse response = handler.handleValidationErrors(exception, exchange);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Error de validación", response.message());
        assertEquals(2, response.details().size());
        assertTrue(response.details().contains("email: El email no tiene un formato válido"));
        assertTrue(response.details().contains("password: La contraseña es obligatoria"));
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
