package com.sgo.presentation.controller;

import com.sgo.application.dto.MedalhaPorPaisDto;
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

@Controller("/relatorios")
@Produces(MediaType.APPLICATION_JSON)
@Secured({"ADMIN", "USUARIO"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Relatórios")
public class RelatorioController {

    private final SistemaOlimpiadasFacade facade;

    public RelatorioController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Get("/medalhas")
    @Operation(summary = "Relatório de medalhas por país")
    public List<MedalhaPorPaisDto> medalhas() {
        return facade.relatorioMedalhas();
    }
}
