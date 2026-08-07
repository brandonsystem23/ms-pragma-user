package com.plazoleta.users_service.infrastructure.security.jwt;

import com.plazoleta.users_service.infrastructure.security.adapter.JwtProviderAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationManager
        implements ReactiveAuthenticationManager {

    private final JwtProviderAdapter jwtProviderAdapter;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String token = authentication.getCredentials().toString();

        if (!jwtProviderAdapter.validate(token)) {
            return Mono.error(new BadCredentialsException("Token inválido"));
        }

        String documento = jwtProviderAdapter.getDocumento(token);

        String role = jwtProviderAdapter.getRole(token);

        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        documento,
                        token,
                        List.of(new SimpleGrantedAuthority(role))
                )
        );
    }
}
