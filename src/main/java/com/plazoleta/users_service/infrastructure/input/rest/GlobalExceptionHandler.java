package com.plazoleta.users_service.infrastructure.input.rest;

import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ErrorResponse handleInvalidCredentials(Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateDocumentException.class})
    public ErrorResponse handleDuplicateData(RuntimeException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ErrorResponse handleRoleNotFound(RoleNotFoundException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex, ServerWebExchange exchange) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para acceder a este recurso",
                exchange,
                List.of()
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ErrorResponse handleValidationErrors(WebExchangeBindException ex, ServerWebExchange exchange) {
        List<String> details = ex.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                exchange,
                details
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor",
                exchange,
                List.of()
        );
    }

    private ErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            ServerWebExchange exchange,
            List<String> details
    ) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .details(details)
                .build();
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
