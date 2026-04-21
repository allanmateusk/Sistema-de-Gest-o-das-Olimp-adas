package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String senha
) {
}
