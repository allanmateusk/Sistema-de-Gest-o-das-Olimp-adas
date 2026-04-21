package com.sgo.application.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

@Serdeable
public record MedalhaPorPaisDto(UUID paisId, String paisNome, long ouro, long prata, long bronze) {
}
