package com.plazoleta.users_service.infrastructure.security.session;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

class BearerTokenAuthenticationConverterTest {

    private final BearerTokenAuthenticationConverter converter = new BearerTokenAuthenticationConverter();

    @Test
    void shouldConvertBearerTokenSuccessfully() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token-123")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(converter.convert(exchange))
                .assertNext(authentication -> {
                    assert authentication != null;
                    assert "token-123".equals(authentication.getCredentials());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenAuthorizationHeaderIsMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(converter.convert(exchange))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenAuthorizationHeaderIsNotBearer() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Basic abc123")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(converter.convert(exchange))
                .verifyComplete();
    }
}
