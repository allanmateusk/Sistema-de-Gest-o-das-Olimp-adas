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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

        // O método está simples e deixa a regra de listagem dentro da facade.
        // Isso é bom, porque o controller fica responsável só por receber a requisição e devolver a resposta.

        return facade.listarCompeticoes();
    }

    @Post
    @Secured("ADMIN")
    @Operation(summary = "Cadastrar competição")
    @ApiResponse(responseCode = "201", description = "Competição criada (Location: recurso com o id no corpo).")
    public HttpResponse<IdResponse> criar(@Body @Valid CompeticaoRequest request) {

        // O uso de @Valid está correto, porque força a validação dos dados antes de chamar a regra de cadastro.
        // Isso evita que dados inválidos cheguem no caso de uso.

        UUID id = facade.cadastrarCompeticao(
                request.nome(),
                request.modalidade(),
                request.dataInicio(),
                request.dataFim()
        );

        // A resposta 201 está adequada para criação de recurso.
        // Uma melhoria seria adicionar o header Location com a rota do recurso criado,
        // já que a própria descrição do Swagger cita isso.

        return HttpResponse.created(new IdResponse(id));
    }

    // A separação de permissões ficou clara: ADMIN pode criar e ADMIN/USUARIO podem listar.
    // Seria interessante documentar no Swagger também possíveis respostas de erro, como 400, 401, 403 e 409.
}
