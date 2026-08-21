package com.plazoleta.users_service.infrastructure.security.session;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.spi.IAuthCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SessionAuthenticationManager implements ReactiveAuthenticationManager {

    private final IAuthCachePort iAuthCachePort;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return iAuthCachePort.findByToken(token)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Token inválido o expirado")))
                .map(this::buildAuthentication);
    }

    private Authentication buildAuthentication(AuthSession session) {
        return new UsernamePasswordAuthenticationToken(
                session.userId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + session.role()))
        );
    }
}
