package com.sgo.presentation.controller;

import com.sgo.application.usecase.AutenticarUsuarioUseCase;
import com.sgo.facade.SistemaOlimpiadasFacade;
import com.sgo.presentation.dto.LoginRequest;
import com.sgo.presentation.dto.LoginResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação")
public class AuthController {

    private final SistemaOlimpiadasFacade facade;

    public AuthController(SistemaOlimpiadasFacade facade) {
        this.facade = facade;
    }

    @Post("/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Operation(summary = "Login com email e senha", description = "Retorna JWT Bearer para uso nas demais rotas.")
    public HttpResponse<LoginResponse> login(@Body @Valid LoginRequest request) {
        AutenticarUsuarioUseCase.LoginResult result = facade.login(request.email(), request.senha());
        LoginResponse body = new LoginResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn(),
                result.perfil()
        );
        return HttpResponse.ok(body);
    }
}
