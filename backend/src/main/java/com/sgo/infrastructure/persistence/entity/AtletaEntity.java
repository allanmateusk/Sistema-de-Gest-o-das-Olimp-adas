package com.sgo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "atletas")
public class AtletaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pais_id", nullable = false)
    private PaisEntity pais;

    // O uso do Lombok com @Getter e @Setter evita a repetição dos métodos getters e setters.
    // Como esses métodos não tinham nenhuma regra específica, a classe fica menor e mais fácil de ler.
    // Só é bom evitar o uso de @Data em entidades JPA, porque ele gera toString, equals e hashCode automaticamente,
    // e isso pode causar problema com relacionamentos LAZY, como o campo pais.
}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PaisEntity getPais() {
        return pais;
    }

    public void setPais(PaisEntity pais) {
        this.pais = pais;
    }
}
