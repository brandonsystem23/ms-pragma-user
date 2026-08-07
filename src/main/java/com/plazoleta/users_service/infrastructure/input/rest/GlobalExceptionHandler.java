package com.plazoleta.users_service.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ServerHttpRequest request = exchange.getRequest();
        ErrorResponse errorResponse;

        if (ex instanceof UserNotFoundException) {
            errorResponse = buildErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage(),
                    request.getPath().value(),
                    List.of()
            );
        } else if (ex instanceof InvalidCredentialsException || ex instanceof BadCredentialsException) {
            errorResponse = buildErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    ex.getMessage(),
                    request.getPath().value(),
                    List.of()
            );
        } else if (ex instanceof DuplicateEmailException || ex instanceof DuplicateDocumentException) {
            errorResponse = buildErrorResponse(
                    HttpStatus.CONFLICT,
                    ex.getMessage(),
                    request.getPath().value(),
                    List.of()
            );
        } else if (ex instanceof RoleNotFoundException) {
            errorResponse = buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getMessage(),
                    request.getPath().value(),
                    List.of()
            );
        } else if (ex instanceof AccessDeniedException) {
            errorResponse = buildErrorResponse(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos para acceder a este recurso",
                    request.getPath().value(),
                    List.of()
            );
        } else if (ex instanceof WebExchangeBindException bindException) {
            List<String> details = bindException.getFieldErrors()
                    .stream()
                    .map(this::formatFieldError)
                    .toList();

            errorResponse = buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Error de validación",
                    request.getPath().value(),
                    details
            );
        } else if (ex instanceof IllegalArgumentException) {
            errorResponse = buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage(),
                    request.getPath().value(),
                    List.of()
            );
        } else {
            errorResponse = buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocurrió un error interno en el servidor",
                    request.getPath().value(),
                    List.of()
            );
        }

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorResponse.status()));
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            byte[] bytes = """
                    {"status":500,"error":"Internal Server Error","message":"Error serializando la respuesta"}
                    """.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }

    private ErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            List<String> details
    ) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .details(details)
                .build();
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
