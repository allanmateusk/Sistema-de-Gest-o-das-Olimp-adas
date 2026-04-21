package com.sgo.presentation.controller;

import com.sgo.facade.SistemaOlimpiadasFacade;
import com.sgo.presentation.dto.IdResponse;
import com.sgo.presentation.dto.InscricaoRequest;
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

@Controller("/inscricoes")
@Produces(MediaType.APPLICATION_JSON)
@Secured("ADMIN")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inscrições")
public class InscricaoController {

    private final SistemaOlimpiadasFacade facade;

    public InscricaoController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Post
    @Operation(summary = "Inscrever atleta em competição")
    public HttpResponse<IdResponse> inscrever(@Body @Valid InscricaoRequest request) {
        UUID id = facade.inscreverAtleta(request.atletaId(), request.competicaoId());
        return HttpResponse.created(new IdResponse(id));
    }
}
