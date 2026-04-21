package com.sgo.facade;

import com.sgo.application.dto.AtletaResponse;
import com.sgo.application.dto.CompeticaoResponse;
import com.sgo.application.dto.LocalResponse;
import com.sgo.application.dto.MedalhaPorPaisDto;
import com.sgo.application.usecase.AlocarLocalUseCase;
import com.sgo.application.usecase.AutenticarUsuarioUseCase;
import com.sgo.application.usecase.CadastrarCompeticaoUseCase;
import com.sgo.application.usecase.GerarRelatorioUseCase;
import com.sgo.application.usecase.InscreverAtletaUseCase;
import com.sgo.application.usecase.ListarAtletasUseCase;
import com.sgo.application.usecase.ListarCompeticoesUseCase;
import com.sgo.application.usecase.ListarLocaisUseCase;
import com.sgo.application.usecase.RegistrarResultadoUseCase;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Orquestra casos de uso expostos pela API, mantendo controllers finos.
 */
@Singleton
public class SistemaOlimpiadasFacade {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final CadastrarCompeticaoUseCase cadastrarCompeticaoUseCase;
    private final InscreverAtletaUseCase inscreverAtletaUseCase;
    private final AlocarLocalUseCase alocarLocalUseCase;
    private final RegistrarResultadoUseCase registrarResultadoUseCase;
    private final GerarRelatorioUseCase gerarRelatorioUseCase;
    private final ListarCompeticoesUseCase listarCompeticoesUseCase;
    private final ListarLocaisUseCase listarLocaisUseCase;
    private final ListarAtletasUseCase listarAtletasUseCase;

    public SistemaOlimpiadasFacade(
            AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            CadastrarCompeticaoUseCase cadastrarCompeticaoUseCase,
            InscreverAtletaUseCase inscreverAtletaUseCase,
            AlocarLocalUseCase alocarLocalUseCase,
            RegistrarResultadoUseCase registrarResultadoUseCase,
            GerarRelatorioUseCase gerarRelatorioUseCase,
            ListarCompeticoesUseCase listarCompeticoesUseCase,
            ListarLocaisUseCase listarLocaisUseCase,
            ListarAtletasUseCase listarAtletasUseCase
    ) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.cadastrarCompeticaoUseCase = cadastrarCompeticaoUseCase;
        this.inscreverAtletaUseCase = inscreverAtletaUseCase;
        this.alocarLocalUseCase = alocarLocalUseCase;
        this.registrarResultadoUseCase = registrarResultadoUseCase;
        this.gerarRelatorioUseCase = gerarRelatorioUseCase;
        this.listarCompeticoesUseCase = listarCompeticoesUseCase;
        this.listarLocaisUseCase = listarLocaisUseCase;
        this.listarAtletasUseCase = listarAtletasUseCase;
    }

    public AutenticarUsuarioUseCase.LoginResult login(String email, String senha) {
        return autenticarUsuarioUseCase.execute(email, senha);
    }

    public UUID cadastrarCompeticao(String nome, String modalidade, Instant dataInicio, Instant dataFim) {
        return cadastrarCompeticaoUseCase.execute(nome, modalidade, dataInicio, dataFim);
    }

    public UUID inscreverAtleta(UUID atletaId, UUID competicaoId) {
        return inscreverAtletaUseCase.execute(atletaId, competicaoId);
    }

    public UUID alocarLocal(UUID competicaoId, UUID localId) {
        return alocarLocalUseCase.execute(competicaoId, localId);
    }

    public UUID registrarResultado(UUID competicaoId, UUID atletaId, Integer posicao) {
        return registrarResultadoUseCase.execute(competicaoId, atletaId, posicao);
    }

    public List<MedalhaPorPaisDto> relatorioMedalhas() {
        return gerarRelatorioUseCase.execute();
    }

    public List<CompeticaoResponse> listarCompeticoes() {
        return listarCompeticoesUseCase.execute();
    }

    public List<LocalResponse> listarLocais() {
        return listarLocaisUseCase.execute();
    }

    public List<AtletaResponse> listarAtletas() {
        return listarAtletasUseCase.execute();
    }
}
