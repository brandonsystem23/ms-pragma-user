package com.plazoleta.users_service.infrastructure.out.jwt.adapter;

import com.plazoleta.users_service.domain.model.auth.AuthSession;
import com.plazoleta.users_service.domain.spi.IJwtProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtProviderAdapter implements IJwtProviderPort {

    private final String secret;
    private final Duration expiration;

    private SecretKey secretKey;

    public JwtProviderAdapter(
            @Value("${auth.token.secret}") String secret,
            Duration expiration
    ) {
        this.secret = secret;
        this.expiration = expiration;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(AuthSession authSession) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(authSession.userId()))
                .claim("userId", authSession.userId())
                .claim("fullName", authSession.fullName())
                .claim("role", authSession.role())
                .claim("numberDocument", authSession.numberDocument())
                .claim("phone", authSession.phone())
                .claim("email", authSession.email())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public AuthSession validateAndGetSession(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return AuthSession.builder()
                .userId(claims.get("userId", Long.class))
                .fullName(claims.get("fullName", String.class))
                .role(claims.get("role", String.class))
                .numberDocument(claims.get("numberDocument", String.class))
                .phone(claims.get("phone", String.class))
                .email(claims.get("email", String.class))
                .build();
    }
}
