package com.sgo.application.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

@Serdeable
public record CompeticaoResponse(
        UUID id,
        String nome,
        String modalidade,
        Instant dataInicio,
        Instant dataFim,
        UUID localId
) {
}
