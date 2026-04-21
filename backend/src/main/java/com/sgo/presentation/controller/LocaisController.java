package com.sgo.presentation.controller;

import com.sgo.application.dto.LocalResponse;
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

@Controller("/locais")
@Produces(MediaType.APPLICATION_JSON)
@Secured({"ADMIN", "USUARIO"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Locais")
public class LocaisController {

    private final SistemaOlimpiadasFacade facade;

    public LocaisController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Get
    @Operation(summary = "Listar locais")
    public List<LocalResponse> listar() {
        return facade.listarLocais();
    }
}
