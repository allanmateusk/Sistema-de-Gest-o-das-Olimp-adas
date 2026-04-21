package com.sgo.infrastructure.security;

import com.sgo.domain.model.PerfilUsuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Singleton
public class JwtTokenService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtTokenService(
            @io.micronaut.context.annotation.Property(name = "micronaut.security.token.jwt.signatures.secret.generator.secret")
            String secret,
            @io.micronaut.context.annotation.Property(name = "sgo.jwt.expiration-minutes")
            long expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public long expiresInSeconds() {
        return expirationMinutes * 60;
    }

    public String generate(@NotNull UUID userId, @NotNull String email, @NotNull PerfilUsuario perfil) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .claim("uid", userId.toString())
                .claim("roles", List.of(perfil.name()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }
}
