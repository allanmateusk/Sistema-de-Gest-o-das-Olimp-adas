package com.sgo.presentation.controller;

import com.sgo.application.dto.AtletaResponse;
import com.sgo.facade.SistemaOlimpiadasFacade;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Controller("/atletas")
@Produces(MediaType.APPLICATION_JSON)
@Secured({"ADMIN", "USUARIO"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Atletas")
public class AtletasController {

    private final SistemaOlimpiadasFacade facade;

    public AtletasController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Get
    @Operation(summary = "Listar atletas")
    public List<AtletaResponse> listar() {
        return facade.listarAtletas();
    }
}
