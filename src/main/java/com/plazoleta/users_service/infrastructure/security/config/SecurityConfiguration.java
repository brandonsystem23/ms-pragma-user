package com.plazoleta.users_service.infrastructure.security.config;

import com.plazoleta.users_service.infrastructure.security.adapter.JwtProviderAdapter;
import com.plazoleta.users_service.infrastructure.security.jwt.JwtAuthenticationConverter;
import com.plazoleta.users_service.infrastructure.security.jwt.JwtAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfiguration {

    private final JwtProviderAdapter jwtProviderAdapter;

    public SecurityConfiguration(JwtProviderAdapter jwtProviderAdapter) {
        this.jwtProviderAdapter = jwtProviderAdapter;
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(
            ServerHttpSecurity http) {

        AuthenticationWebFilter authenticationWebFilter =
                new AuthenticationWebFilter(
                        new JwtAuthenticationManager(jwtProviderAdapter));

        authenticationWebFilter.setServerAuthenticationConverter(
                new JwtAuthenticationConverter());

        authenticationWebFilter.setRequiresAuthenticationMatcher(

                ServerWebExchangeMatchers.pathMatchers("/api/**")

        );

        return http

                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchange -> exchange

                        .pathMatchers("/api/v1/auth/login").permitAll()

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/users/owners"
                        ).hasRole("ADMIN")

                        .anyExchange().authenticated()
                )

                .addFilterAt(

                        authenticationWebFilter,

                        SecurityWebFiltersOrder.AUTHENTICATION

                )

                .build();

    }

}
