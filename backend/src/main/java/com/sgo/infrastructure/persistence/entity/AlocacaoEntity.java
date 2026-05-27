package com.sgo.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "alocacoes")
public class AlocacaoEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competicao_id", nullable = false, unique = true)
    private CompeticaoEntity competicao;

    // O unique = true em competicao_id deixa claro que cada competição só pode ter uma alocação.
    // Essa regra faz sentido, mas seria bom conferir se ela também está documentada na regra de negócio do sistema.

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "local_id", nullable = false)
    private LocalEntity local;

    // O uso de FetchType.LAZY está adequado aqui, porque evita carregar competição e local sem necessidade.
    // Isso ajuda principalmente quando a listagem de alocações crescer.

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {

        // Seria interessante validar se o id está nulo antes de atribuir.
        // Isso evita criar uma entidade sem identificador válido.

        this.id = id;
    }

    public CompeticaoEntity getCompeticao() {
        return competicao;
    }

    public void setCompeticao(CompeticaoEntity competicao) {

        // Como a competição é obrigatória no banco, também seria bom evitar receber null aqui.
        // Isso deixa a regra mais clara dentro da própria entidade.

        this.competicao = competicao;
    }

    public LocalEntity getLocal() {
        return local;
    }

    public void setLocal(LocalEntity local) {

        // O local também é obrigatório, então poderia ter uma validação simples para impedir valor nulo.
        // Assim o erro aparece antes de tentar salvar no banco.

        this.local = local;
    }
}
