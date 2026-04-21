package com.sgo.application.usecase;

import com.sgo.application.dto.AtletaResponse;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

@Singleton
public class ListarAtletasUseCase {

    private final AtletaRepository atletaRepository;

    public ListarAtletasUseCase(AtletaRepository atletaRepository) {
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public List<AtletaResponse> execute() {
        return atletaRepository.findAll().stream().map(this::toDto).toList();
    }

    private AtletaResponse toDto(AtletaEntity e) {
        return new AtletaResponse(
                e.getId(),
                e.getNome(),
                e.getPais().getId(),
                e.getPais().getNome()
        );
    }
}
