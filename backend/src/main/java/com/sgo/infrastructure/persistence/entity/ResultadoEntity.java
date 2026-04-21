package com.sgo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
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
        name = "resultados",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"competicao_id", "atleta_id"}),
                @UniqueConstraint(columnNames = {"competicao_id", "posicao"})
        }
)
public class ResultadoEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competicao_id", nullable = false)
    private CompeticaoEntity competicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atleta_id", nullable = false)
    private AtletaEntity atleta;

    @Column(nullable = false)
    private Integer posicao;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CompeticaoEntity getCompeticao() {
        return competicao;
    }

    public void setCompeticao(CompeticaoEntity competicao) {
        this.competicao = competicao;
    }

    public AtletaEntity getAtleta() {
        return atleta;
    }

    public void setAtleta(AtletaEntity atleta) {
        this.atleta = atleta;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }
}
