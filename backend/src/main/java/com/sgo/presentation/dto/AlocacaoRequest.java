package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
public record AlocacaoRequest(
        @NotNull UUID competicaoId,
        @NotNull UUID localId

        // O uso de @NotNull está correto, porque os dois campos são obrigatórios para fazer a alocação.
        // Uma melhoria seria adicionar mensagens personalizadas na validação, para o erro ficar mais claro para quem usa a API.
        // Exemplo: @NotNull(message = "O id da competição é obrigatório")
) {
    // O uso de record está bom aqui, porque esse DTO só carrega dados da requisição.
    // Isso evita criar getters, construtor e outros métodos sem necessidade.
}
