package com.sgo.infrastructure.persistence.repository;

import com.sgo.infrastructure.persistence.entity.ResultadoEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultadoRepository extends JpaRepository<ResultadoEntity, UUID> {

    Optional<ResultadoEntity> findByCompeticao_IdAndPosicao(UUID competicaoId, Integer posicao);

    Optional<ResultadoEntity> findByCompeticao_IdAndAtleta_Id(UUID competicaoId, UUID atletaId);
}
