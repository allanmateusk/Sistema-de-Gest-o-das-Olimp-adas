package com.sgo.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "inscricoes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"atleta_id", "competicao_id"})
)
public class InscricaoEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atleta_id", nullable = false)
    private AtletaEntity atleta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competicao_id", nullable = false)
    private CompeticaoEntity competicao;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AtletaEntity getAtleta() {
        return atleta;
    }

    public void setAtleta(AtletaEntity atleta) {
        this.atleta = atleta;
    }

    public CompeticaoEntity getCompeticao() {
        return competicao;
    }

    public void setCompeticao(CompeticaoEntity competicao) {
        this.competicao = competicao;
    }
}
