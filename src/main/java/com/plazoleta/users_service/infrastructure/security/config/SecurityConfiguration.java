package com.plazoleta.users_service.infrastructure.security.config;

import com.plazoleta.users_service.domain.model.RoleNames;
import com.plazoleta.users_service.domain.port.out.AuthSessionPort;
import com.plazoleta.users_service.infrastructure.security.handler.JsonAccessDeniedHandler;
import com.plazoleta.users_service.infrastructure.security.handler.JsonAuthenticationEntryPoint;
import com.plazoleta.users_service.infrastructure.security.session.BearerTokenAuthenticationConverter;
import com.plazoleta.users_service.infrastructure.security.session.SessionAuthenticationManager;
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

    private final AuthSessionPort authSessionPort;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;

    public SecurityConfiguration(
            AuthSessionPort authSessionPort,
            JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint,
            JsonAccessDeniedHandler jsonAccessDeniedHandler
    ) {
        this.authSessionPort = authSessionPort;
        this.jsonAuthenticationEntryPoint = jsonAuthenticationEntryPoint;
        this.jsonAccessDeniedHandler = jsonAccessDeniedHandler;
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        AuthenticationWebFilter authenticationWebFilter =
                new AuthenticationWebFilter(
                        new SessionAuthenticationManager(authSessionPort)
                );

        authenticationWebFilter.setServerAuthenticationConverter(
                new BearerTokenAuthenticationConverter()
        );

        authenticationWebFilter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers("/api/**")
        );

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler)
                )
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/v1/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/clients/self-register").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/owners").hasRole(RoleNames.ADMIN)
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/employees").hasRole(RoleNames.OWNER)
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
