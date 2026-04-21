package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
public record AlocacaoRequest(
        @NotNull UUID competicaoId,
        @NotNull UUID localId
) {
}
