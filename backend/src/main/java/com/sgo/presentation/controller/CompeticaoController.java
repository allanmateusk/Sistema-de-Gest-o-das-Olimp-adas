package com.sgo.presentation.controller;

import com.sgo.application.dto.CompeticaoResponse;
import com.sgo.facade.SistemaOlimpiadasFacade;
import com.sgo.presentation.dto.CompeticaoRequest;
import com.sgo.presentation.dto.IdResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@Controller("/competicoes")
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Competições")
public class CompeticaoController {

    private final SistemaOlimpiadasFacade facade;

    public CompeticaoController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Get
    @Secured({"ADMIN", "USUARIO"})
    @Operation(summary = "Listar competições")
    public List<CompeticaoResponse> listar() {
        return facade.listarCompeticoes();
    }

    @Post
    @Secured("ADMIN")
    @Operation(summary = "Cadastrar competição")
    public HttpResponse<IdResponse> criar(@Body @Valid CompeticaoRequest request) {
        UUID id = facade.cadastrarCompeticao(
                request.nome(),
                request.modalidade(),
                request.dataInicio(),
                request.dataFim()
        );
        return HttpResponse.created(new IdResponse(id));
    }
}
