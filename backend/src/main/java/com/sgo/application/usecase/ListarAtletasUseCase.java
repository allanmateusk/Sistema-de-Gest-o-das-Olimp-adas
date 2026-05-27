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

        // Como esse método apenas lista atletas, talvez o @Transactional não seja necessário.
        // Se não existe alteração no banco, o método pode ficar sem essa anotação para indicar que é apenas uma consulta.

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
