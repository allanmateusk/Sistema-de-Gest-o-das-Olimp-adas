package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.InscricaoEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.InscricaoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class InscreverAtletaUseCase {

    private final AtletaRepository atletaRepository;
    private final CompeticaoRepository competicaoRepository;
    private final InscricaoRepository inscricaoRepository;

    public InscreverAtletaUseCase(
            AtletaRepository atletaRepository,
            CompeticaoRepository competicaoRepository,
            InscricaoRepository inscricaoRepository
    ) {
        this.atletaRepository = atletaRepository;
        this.competicaoRepository = competicaoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    @Transactional
    public UUID execute(UUID atletaId, UUID competicaoId) {
        AtletaEntity atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new NotFoundException("Atleta não encontrado"));
        CompeticaoEntity competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new NotFoundException("Competição não encontrada"));
        if (inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(atletaId, competicaoId)) {
            throw new BusinessException("Atleta já inscrito nesta competição");
        }
        InscricaoEntity inscricao = new InscricaoEntity();
        inscricao.setId(UUID.randomUUID());
        inscricao.setAtleta(atleta);
        inscricao.setCompeticao(competicao);
        inscricaoRepository.save(inscricao);
        return inscricao.getId();
    }
}
