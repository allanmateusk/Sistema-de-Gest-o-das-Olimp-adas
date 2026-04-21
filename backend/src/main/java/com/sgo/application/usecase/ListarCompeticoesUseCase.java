package com.sgo.application.usecase;

import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.application.dto.CompeticaoResponse;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

@Singleton
public class ListarCompeticoesUseCase {

    private final CompeticaoRepository competicaoRepository;

    public ListarCompeticoesUseCase(CompeticaoRepository competicaoRepository) {
        this.competicaoRepository = competicaoRepository;
    }

    @Transactional
    public List<CompeticaoResponse> execute() {
        return competicaoRepository.findAll().stream().map(this::toDto).toList();
    }

    private CompeticaoResponse toDto(CompeticaoEntity e) {
        return new CompeticaoResponse(
                e.getId(),
                e.getNome(),
                e.getModalidade(),
                e.getDataInicio(),
                e.getDataFim(),
                e.getLocal() != null ? e.getLocal().getId() : null
        );
    }
}
