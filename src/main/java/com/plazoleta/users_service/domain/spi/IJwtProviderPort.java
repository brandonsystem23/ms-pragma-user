package com.plazoleta.users_service.domain.spi;

import com.plazoleta.users_service.domain.model.auth.AuthSession;

public interface IJwtProviderPort {

    String generateToken(AuthSession authSession);

    AuthSession validateAndGetSession(String token);
}
