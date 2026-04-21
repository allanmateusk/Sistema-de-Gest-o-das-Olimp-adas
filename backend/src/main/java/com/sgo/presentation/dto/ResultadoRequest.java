package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Serdeable
public record ResultadoRequest(
        @NotNull UUID competicaoId,
        @NotNull UUID atletaId,
        @NotNull @Positive Integer posicao
) {
}
