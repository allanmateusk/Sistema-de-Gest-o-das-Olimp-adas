package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@Singleton
public class CadastrarCompeticaoUseCase {

    private final CompeticaoRepository competicaoRepository;

    public CadastrarCompeticaoUseCase(CompeticaoRepository competicaoRepository) {
        this.competicaoRepository = competicaoRepository;
    }

    @Transactional
    public UUID execute(String nome, String modalidade, Instant dataInicio, Instant dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("dataInicio não pode ser posterior a dataFim");
        }
        CompeticaoEntity e = new CompeticaoEntity();
        e.setId(UUID.randomUUID());
        e.setNome(nome);
        e.setModalidade(modalidade);
        e.setDataInicio(dataInicio);
        e.setDataFim(dataFim);
        competicaoRepository.save(e);
        return e.getId();
    }
}
