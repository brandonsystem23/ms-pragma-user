package com.plazoleta.users_service.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.BadCredentialsException;
import reactor.test.StepVerifier;


import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonAuthenticationEntryPointTest {

    @Test
    void shouldWriteUnauthorizedResponse() {
        JsonAuthenticationEntryPoint entryPoint = new JsonAuthenticationEntryPoint(new ObjectMapper());

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(entryPoint.commence(exchange, new BadCredentialsException("Invalid token")))
                .verifyComplete();

        assertTrue(exchange.getResponse().getStatusCode().value() == 401);
    }
}
