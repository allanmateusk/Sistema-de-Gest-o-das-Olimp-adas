package com.sgo.infrastructure.persistence.repository;

import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Repository
public interface AtletaRepository extends JpaRepository<AtletaEntity, UUID> {
}
