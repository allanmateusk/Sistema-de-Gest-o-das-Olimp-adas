package com.sgo.infrastructure.persistence.repository;

import com.sgo.infrastructure.persistence.entity.LocalEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Repository
public interface LocalRepository extends JpaRepository<LocalEntity, UUID> {
}
