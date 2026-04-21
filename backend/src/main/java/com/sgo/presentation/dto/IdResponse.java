package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

@Serdeable
public record IdResponse(UUID id) {
}
