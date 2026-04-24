package com.sgo.application.usecase;

import com.sgo.domain.exception.UnauthorizedException;
import com.sgo.domain.model.PerfilUsuario;
import com.sgo.infrastructure.persistence.entity.UsuarioEntity;
import com.sgo.infrastructure.persistence.repository.UsuarioRepository;
import com.sgo.infrastructure.security.JwtTokenService;
import com.sgo.infrastructure.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioUseCaseTest {

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    PasswordHasher passwordHasher;
    @Mock
    JwtTokenService jwtTokenService;

    @Test
    void retornaTokenQuandoSenhaConfere() {
        UUID id = UUID.randomUUID();
        var user = new UsuarioEntity();
        user.setId(id);
        user.setEmail("a@a.local");
        user.setSenhaHash("hash");
        user.setPerfil(PerfilUsuario.ADMIN);
        when(usuarioRepository.findByEmail("a@a.local")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("secret", "hash")).thenReturn(true);
        when(jwtTokenService.generate(eq(id), eq("a@a.local"), eq(PerfilUsuario.ADMIN))).thenReturn("tok");
        when(jwtTokenService.expiresInSeconds()).thenReturn(3600L);
        var useCase = new AutenticarUsuarioUseCase(usuarioRepository, passwordHasher, jwtTokenService);
        var r = useCase.execute("a@a.local", "secret");
        assertEquals("tok", r.accessToken());
        assertEquals(3600L, r.expiresIn());
        assertEquals(PerfilUsuario.ADMIN, r.perfil());
    }

    @Test
    void lancaSeEmailNaoExiste() {
        when(usuarioRepository.findByEmail("x@x.local")).thenReturn(Optional.empty());
        var useCase = new AutenticarUsuarioUseCase(usuarioRepository, passwordHasher, jwtTokenService);
        assertThrows(UnauthorizedException.class, () -> useCase.execute("x@x.local", "x"));
        verify(jwtTokenService, never()).generate(any(), any(), any());
    }

    @Test
    void lancaSeSenhaInvalida() {
        var user = new UsuarioEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("a@a.local");
        user.setSenhaHash("h");
        user.setPerfil(PerfilUsuario.USUARIO);
        when(usuarioRepository.findByEmail("a@a.local")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("bad", "h")).thenReturn(false);
        var useCase = new AutenticarUsuarioUseCase(usuarioRepository, passwordHasher, jwtTokenService);
        assertThrows(UnauthorizedException.class, () -> useCase.execute("a@a.local", "bad"));
    }
}
