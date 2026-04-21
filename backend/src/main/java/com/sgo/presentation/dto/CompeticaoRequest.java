package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Serdeable
public record CompeticaoRequest(
        @NotBlank String nome,
        String modalidade,
        @NotNull Instant dataInicio,
        @NotNull Instant dataFim
) {
}
