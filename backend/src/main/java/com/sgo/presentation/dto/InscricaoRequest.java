package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
public record InscricaoRequest(
        @NotNull UUID atletaId,
        @NotNull UUID competicaoId
) {
}
