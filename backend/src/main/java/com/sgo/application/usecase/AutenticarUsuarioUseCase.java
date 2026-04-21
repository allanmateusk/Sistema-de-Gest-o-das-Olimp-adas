package com.sgo.application.usecase;

import com.sgo.domain.exception.UnauthorizedException;
import com.sgo.domain.model.PerfilUsuario;
import com.sgo.infrastructure.persistence.entity.UsuarioEntity;
import com.sgo.infrastructure.persistence.repository.UsuarioRepository;
import com.sgo.infrastructure.security.JwtTokenService;
import com.sgo.infrastructure.security.PasswordHasher;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final JwtTokenService jwtTokenService;

    public AutenticarUsuarioUseCase(
            UsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher,
            JwtTokenService jwtTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public LoginResult execute(String email, String senha) {
        UsuarioEntity user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));
        if (!passwordHasher.matches(senha, user.getSenhaHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }
        String token = jwtTokenService.generate(user.getId(), user.getEmail(), user.getPerfil());
        return new LoginResult(token, "Bearer", user.getPerfil(), jwtTokenService.expiresInSeconds());
    }

    public record LoginResult(String accessToken, String tokenType, PerfilUsuario perfil, long expiresIn) {
    }
}
