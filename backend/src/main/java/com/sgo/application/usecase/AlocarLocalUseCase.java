package com.sgo.application.usecase;

import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AlocacaoEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.repository.AlocacaoRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.LocalRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class AlocarLocalUseCase {

    private final CompeticaoRepository competicaoRepository;
    private final LocalRepository localRepository;
    private final AlocacaoRepository alocacaoRepository;

    public AlocarLocalUseCase(
            CompeticaoRepository competicaoRepository,
            LocalRepository localRepository,
            AlocacaoRepository alocacaoRepository
    ) {
        this.competicaoRepository = competicaoRepository;
        this.localRepository = localRepository;
        this.alocacaoRepository = alocacaoRepository;
    }

    @Transactional
    public UUID execute(UUID competicaoId, UUID localId) {
        CompeticaoEntity competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new NotFoundException("Competição não encontrada"));
        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new NotFoundException("Local não encontrado"));

        AlocacaoEntity alocacao = alocacaoRepository.findByCompeticao_Id(competicaoId).orElse(null);
        if (alocacao == null) {
            alocacao = new AlocacaoEntity();
            alocacao.setId(UUID.randomUUID());
            alocacao.setCompeticao(competicao);
        }
        alocacao.setLocal(local);
        competicao.setLocal(local);
        alocacaoRepository.save(alocacao);
        competicaoRepository.save(competicao);
        return alocacao.getId();
    }
}
