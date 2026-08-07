package com.plazoleta.users_service.infrastructure.input.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.users_service.domain.exception.DuplicateDocumentException;
import com.plazoleta.users_service.domain.exception.DuplicateEmailException;
import com.plazoleta.users_service.domain.exception.InvalidCredentialsException;
import com.plazoleta.users_service.domain.exception.RoleNotFoundException;
import com.plazoleta.users_service.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class GlobalExceptionHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    private GlobalExceptionHandler handler;

    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        handler = new GlobalExceptionHandler(objectMapper);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/users")
                .build();

        exchange = MockServerWebExchange.from(request);
    }

    @Test
    void shouldHandleUserNotFoundException() throws Exception {

        UserNotFoundException exception = mock(UserNotFoundException.class);

        when(exception.getMessage())
                .thenReturn("Usuario no encontrado");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("""
                        {
                            "status":404,
                            "error":"Not Found",
                            "message":"Usuario no encontrado",
                            "path":"/api/users"
                        }
                        """.getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.NOT_FOUND,
                exchange.getResponse().getStatusCode()
        );

        verify(objectMapper).writeValueAsBytes(any(ErrorResponse.class));
    }

    @Test
    void shouldHandleInvalidCredentialsException() throws Exception {

        InvalidCredentialsException exception =
                mock(InvalidCredentialsException.class);

        when(exception.getMessage())
                .thenReturn("Credenciales inválidas");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );

        verify(objectMapper).writeValueAsBytes(any(ErrorResponse.class));
    }

    @Test
    void shouldHandleBadCredentialsException() throws Exception {

        BadCredentialsException exception =
                mock(BadCredentialsException.class);

        when(exception.getMessage())
                .thenReturn("Bad credentials");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldHandleDuplicateEmailException() throws Exception {

        DuplicateEmailException exception =
                mock(DuplicateEmailException.class);

        when(exception.getMessage())
                .thenReturn("El email ya está registrado");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.CONFLICT,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldHandleDuplicateDocumentException() throws Exception {

        DuplicateDocumentException exception =
                mock(DuplicateDocumentException.class);

        when(exception.getMessage())
                .thenReturn("El numberDocument ya está registrado");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.CONFLICT,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldHandleRoleNotFoundException() throws Exception {

        RoleNotFoundException exception =
                mock(RoleNotFoundException.class);

        when(exception.getMessage())
                .thenReturn("Rol no encontrado");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldHandleAccessDeniedException() throws Exception {

        AccessDeniedException exception =
                mock(AccessDeniedException.class);

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );

        verify(objectMapper).writeValueAsBytes(any(ErrorResponse.class));
    }

    @Test
    void shouldHandleWebExchangeBindException() throws Exception {

        WebExchangeBindException exception =
                mock(WebExchangeBindException.class);

        FieldError fieldError = new FieldError(
                "userRequest",
                "email",
                "El email electrónico no es válido"
        );

        when(exception.getFieldErrors())
                .thenReturn(List.of(fieldError));

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exchange.getResponse().getStatusCode()
        );

        verify(exception).getFieldErrors();
        verify(objectMapper).writeValueAsBytes(any(ErrorResponse.class));
    }

    @Test
    void shouldHandleIllegalArgumentException() throws Exception {

        IllegalArgumentException exception =
                mock(IllegalArgumentException.class);

        when(exception.getMessage())
                .thenReturn("Argumento inválido");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldHandleUnknownException() throws Exception {

        RuntimeException exception =
                new RuntimeException("Error inesperado");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldHandleObjectMapperSerializationException() throws Exception {

        RuntimeException exception =
                new RuntimeException("Error inesperado");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenThrow(new JsonProcessingException("Error serializando") {
                });

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exchange.getResponse().getStatusCode()
        );

        verify(objectMapper)
                .writeValueAsBytes(any(ErrorResponse.class));
    }

    @Test
    void shouldSetContentTypeToApplicationJson() throws Exception {

        RuntimeException exception =
                new RuntimeException("Error");

        when(objectMapper.writeValueAsBytes(any(ErrorResponse.class)))
                .thenReturn("{}".getBytes(StandardCharsets.UTF_8));

        Mono<Void> result = handler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assertTrue(
                exchange.getResponse()
                        .getHeaders()
                        .getFirst("Content-Type")
                        .contains("application/json")
        );
    }
}
