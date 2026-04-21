package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.ResultadoEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.InscricaoRepository;
import com.sgo.infrastructure.persistence.repository.ResultadoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class RegistrarResultadoUseCase {

    private final CompeticaoRepository competicaoRepository;
    private final AtletaRepository atletaRepository;
    private final InscricaoRepository inscricaoRepository;
    private final ResultadoRepository resultadoRepository;

    public RegistrarResultadoUseCase(
            CompeticaoRepository competicaoRepository,
            AtletaRepository atletaRepository,
            InscricaoRepository inscricaoRepository,
            ResultadoRepository resultadoRepository
    ) {
        this.competicaoRepository = competicaoRepository;
        this.atletaRepository = atletaRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.resultadoRepository = resultadoRepository;
    }

    @Transactional
    public UUID execute(UUID competicaoId, UUID atletaId, Integer posicao) {
        if (posicao == null || posicao < 1) {
            throw new BusinessException("posicao deve ser >= 1");
        }
        CompeticaoEntity competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new NotFoundException("Competição não encontrada"));
        AtletaEntity atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new NotFoundException("Atleta não encontrado"));
        if (!inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(atletaId, competicaoId)) {
            throw new BusinessException("Atleta não está inscrito nesta competição");
        }
        resultadoRepository.findByCompeticao_IdAndAtleta_Id(competicaoId, atletaId).ifPresent(r -> {
            throw new BusinessException("Resultado já registrado para este atleta na competição");
        });
        resultadoRepository.findByCompeticao_IdAndPosicao(competicaoId, posicao).ifPresent(r -> {
            throw new BusinessException("Posição já ocupada nesta competição");
        });

        ResultadoEntity resultado = new ResultadoEntity();
        resultado.setId(UUID.randomUUID());
        resultado.setCompeticao(competicao);
        resultado.setAtleta(atleta);
        resultado.setPosicao(posicao);
        resultadoRepository.save(resultado);
        return resultado.getId();
    }
}
