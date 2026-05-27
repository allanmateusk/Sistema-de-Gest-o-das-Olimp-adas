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
        // Seria bom validar se o secret não está vazio e se tem tamanho suficiente.
        // Como ele é usado para assinar o token, uma chave fraca pode comprometer a segurança da autenticação.

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public long expiresInSeconds() {

        // Essa conta aparece também no método generate.
        // Para evitar repetição, poderia ter um método privado ou constante para converter minutos em segundos.

        return expirationMinutes * 60;
    }

    public String generate(@NotNull UUID userId, @NotNull String email, @NotNull PerfilUsuario perfil) {

        // As anotações @NotNull ajudam, mas ainda seria bom garantir que esses valores não cheguem vazios.
        // No caso do email, por exemplo, poderia validar se ele não está em branco.

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);

        // Seria interessante validar se expirationMinutes é maior que zero.
        // Se vier zero ou negativo na configuração, o token pode expirar no mesmo momento ou ser criado já inválido.

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .claim("uid", userId.toString())
                .claim("roles", List.of(perfil.name()))

                // Os claims estão simples e fáceis de entender.
                // Mesmo assim, seria bom manter os nomes "uid" e "roles" como constantes,
                // para evitar erro de digitação caso sejam usados em outras partes do sistema.

                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();

        // A responsabilidade da classe está bem definida, pois ela cuida apenas da geração do token.
        // Uma melhoria futura seria criar também um método para validar e ler o token,
        // caso essa responsabilidade ainda não exista em outra classe do projeto.
    }
}
