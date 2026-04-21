package com.sgo.application.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

@Serdeable
public record LocalResponse(UUID id, String nome, String cidade, Integer capacidade) {
}
