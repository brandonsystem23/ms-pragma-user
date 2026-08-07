package com.plazoleta.users_service.infrastructure.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(
            ServerWebExchange exchange) {

        String authorization =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            return Mono.empty();
        }

        if (!authorization.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String token = authorization.substring(7);

        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        null,
                        token
                )
        );

    }

}
