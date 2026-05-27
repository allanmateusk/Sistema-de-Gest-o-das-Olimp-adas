package com.sgo.infrastructure.security;

import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Singleton
public class PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // O uso do BCrypt é uma boa escolha para guardar senhas, porque ele já aplica hash de forma segura.
    // Mesmo assim, poderia ser interessante deixar a força do BCrypt configurável,
    // caso o projeto precise ajustar isso no futuro.

    public String hash(String rawPassword) {

        // Seria bom validar se rawPassword não está nula ou vazia antes de gerar o hash.
        // Isso evita salvar uma senha inválida ou causar erro inesperado.

        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hash) {

        // Aqui também poderia ter uma validação simples para rawPassword e hash.
        // Se algum dos dois vier nulo, o método pode falhar em vez de retornar uma resposta controlada.

        return encoder.matches(rawPassword, hash);
    }
}
