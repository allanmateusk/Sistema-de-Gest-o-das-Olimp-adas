package com.sgo.presentation.controller;

import com.sgo.facade.SistemaOlimpiadasFacade;
import com.sgo.presentation.dto.IdResponse;
import com.sgo.presentation.dto.ResultadoRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.UUID;

@Controller("/resultados")
@Produces(MediaType.APPLICATION_JSON)
@Secured("ADMIN")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Resultados")
public class ResultadoController {

    private final SistemaOlimpiadasFacade facade;

    public ResultadoController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Post
    @Operation(summary = "Registrar resultado (posição) na competição")
    public HttpResponse<IdResponse> registrar(@Body @Valid ResultadoRequest request) {
        UUID id = facade.registrarResultado(request.competicaoId(), request.atletaId(), request.posicao());
        return HttpResponse.created(new IdResponse(id));
    }
}
