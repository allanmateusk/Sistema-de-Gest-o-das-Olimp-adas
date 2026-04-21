package com.sgo.infrastructure.persistence.repository;

import com.sgo.infrastructure.persistence.entity.AlocacaoEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlocacaoRepository extends JpaRepository<AlocacaoEntity, UUID> {

    Optional<AlocacaoEntity> findByCompeticao_Id(UUID competicaoId);
}
