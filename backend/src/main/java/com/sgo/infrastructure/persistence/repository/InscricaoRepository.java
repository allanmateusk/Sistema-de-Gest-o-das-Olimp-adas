package com.sgo.infrastructure.persistence.repository;

import com.sgo.infrastructure.persistence.entity.InscricaoEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InscricaoRepository extends JpaRepository<InscricaoEntity, UUID> {

    boolean existsByAtleta_IdAndCompeticao_Id(UUID atletaId, UUID competicaoId);
}
