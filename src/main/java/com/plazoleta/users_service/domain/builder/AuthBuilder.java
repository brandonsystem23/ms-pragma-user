package com.plazoleta.users_service.domain.builder;

import com.plazoleta.users_service.domain.model.User;
import com.plazoleta.users_service.domain.model.auth.AuthResult;
import com.plazoleta.users_service.domain.model.auth.AuthSession;

public final class AuthBuilder {

    private AuthBuilder() {

    }

    public static AuthSession buildAuthSession(User user) {
        return AuthSession.builder()
                .userId(user.getId())
                .fullName(user.getFirstName().concat(" ").concat(user.getLastName()))
                .role(user.getRole().getName())
                .numberDocument(user.getNumberDocument())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    public static AuthResult buildAuthResult(User user, String token) {
        return AuthResult.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .role(user.getRole().getName())
                .build();
    }
}
