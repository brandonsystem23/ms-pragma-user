package com.plazoleta.users_service.infrastructure.security.adapter;

import com.plazoleta.users_service.domain.model.Usuario;
import com.plazoleta.users_service.domain.spi.JwtProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProviderAdapter implements JwtProviderPort {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    public String generateToken(Usuario usuario) {

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Date now = new Date();

        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(usuario.getDocumentoIdentidad())
                .claim("userId", usuario.getId())
                .claim("role", usuario.getRol().getNombre())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();

    }

    public String getDocumento(String token){

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    public boolean validate(String token){

        try{

            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;

        }

    }

    public String getRole(String token){

        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()

                .verifyWith(key)

                .build()

                .parseSignedClaims(token)

                .getPayload()

                .get("role", String.class);

    }

}
