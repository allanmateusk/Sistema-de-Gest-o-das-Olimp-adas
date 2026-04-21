package com.sgo.application.usecase;

import com.sgo.application.dto.LocalResponse;
import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.repository.LocalRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

@Singleton
public class ListarLocaisUseCase {

    private final LocalRepository localRepository;

    public ListarLocaisUseCase(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    @Transactional
    public List<LocalResponse> execute() {
        return localRepository.findAll().stream().map(this::toDto).toList();
    }

    private LocalResponse toDto(LocalEntity e) {
        return new LocalResponse(e.getId(), e.getNome(), e.getCidade(), e.getCapacidade());
    }
}
