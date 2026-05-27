package com.sgo.presentation.controller;

import com.sgo.facade.SistemaOlimpiadasFacade;
import com.sgo.presentation.dto.AlocacaoRequest;
import com.sgo.presentation.dto.IdResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.UUID;

@Controller("/alocacoes")
@Produces(MediaType.APPLICATION_JSON)
@Secured("ADMIN")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Alocações")
public class AlocacaoController {

    private final SistemaOlimpiadasFacade facade;

    public AlocacaoController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Post
    @Operation(summary = "Alocar local à competição")
    @ApiResponse(responseCode = "201", description = "Alocação criada")
    public HttpResponse<IdResponse> alocar(@Body @Valid AlocacaoRequest request) {

        // O controller está bem simples e passa a regra principal para a facade.
        // Isso é positivo, porque evita deixar lógica de negócio dentro da camada de apresentação.

        UUID id = facade.alocarLocal(request.competicaoId(), request.localId());

        // Seria interessante documentar também possíveis respostas de erro no Swagger,
        // como 400 para dados inválidos, 401/403 para acesso não autorizado e 404 caso competição ou local não existam.

        return HttpResponse.created(new IdResponse(id));
    }
}
