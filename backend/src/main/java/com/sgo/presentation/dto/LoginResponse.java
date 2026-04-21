package com.sgo.presentation.dto;

import com.sgo.domain.model.PerfilUsuario;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        PerfilUsuario perfil
) {
}
