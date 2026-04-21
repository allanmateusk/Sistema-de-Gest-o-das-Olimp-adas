package com.sgo.application.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

@Serdeable
public record AtletaResponse(UUID id, String nome, UUID paisId, String paisNome) {
}
