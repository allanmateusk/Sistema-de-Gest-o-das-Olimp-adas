package com.sgo.application.usecase;

import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AlocacaoEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.repository.AlocacaoRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.LocalRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class AlocarLocalUseCase {

    private final CompeticaoRepository competicaoRepository;
    private final LocalRepository localRepository;
    private final AlocacaoRepository alocacaoRepository;

    public AlocarLocalUseCase(
            CompeticaoRepository competicaoRepository,
            LocalRepository localRepository,
            AlocacaoRepository alocacaoRepository
    ) {
        this.competicaoRepository = competicaoRepository;
        this.localRepository = localRepository;
        this.alocacaoRepository = alocacaoRepository;
    }

    @Transactional
    public UUID execute(UUID competicaoId, UUID localId) {

        // Sugestão de melhoria:
        // Seria interessante validar se competicaoId e localId são nulos antes de buscar no banco.
        //
        // Benefícios:
        // - Evita erros inesperados em tempo de execução.
        // - Deixa mais claro quais dados são obrigatórios para executar o caso de uso.
        // - Facilita o tratamento de erro pela API.
        //
        // Sugestão:
        // Adicionar uma validação no início do método, lançando uma exceção caso algum dos IDs seja nulo.

        CompeticaoEntity competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new NotFoundException("Competição não encontrada"));

        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new NotFoundException("Local não encontrado"));

        // Sugestão de arquitetura:
        // Este caso de uso acessa diretamente repositories e entities da camada de infraestrutura.
        // Isso funciona, mas aumenta o acoplamento entre a camada de aplicação e a camada de persistência.
        //
        // Benefícios de separar melhor:
        // - Facilita testes unitários do caso de uso.
        // - Permite trocar a tecnologia de persistência com menos impacto.
        // - Deixa o projeto mais próximo de uma Clean Architecture.
        //
        // Sugestão:
        // Considerar criar interfaces de repositório na camada de application/domain e deixar as implementações
        // concretas apenas na camada infrastructure.

        AlocacaoEntity alocacao = alocacaoRepository.findByCompeticao_Id(competicaoId).orElse(null);

        // Sugestão de melhoria:
        // O uso de orElse(null) funciona, mas reintroduz o uso de null mesmo utilizando Optional.
        // Isso pode deixar o código mais propenso a erros caso novas regras sejam adicionadas futuramente.
        //
        // Benefícios:
        // - Evita verificações manuais com null.
        // - Deixa o fluxo mais legível.
        // - Usa melhor os recursos do Optional.
        //
        // Sugestão:
        // Usar orElseGet para criar uma nova AlocacaoEntity quando ela não existir.

        if (alocacao == null) {
            alocacao = new AlocacaoEntity();
            alocacao.setId(UUID.randomUUID());
            alocacao.setCompeticao(competicao);

            // Sugestão de refatoração:
            // A criação de uma nova alocação está dentro do método execute.
            // Se futuramente a criação da alocação tiver mais regras, esse método pode ficar grande demais.
            //
            // Benefícios:
            // - Melhora a organização do código.
            // - Facilita manutenção.
            // - Deixa o método execute mais focado no fluxo principal.
            //
            // Sugestão:
            // Extrair essa criação para um método privado, como criarNovaAlocacao(competicao).
        }

        alocacao.setLocal(local);
        competicao.setLocal(local);

        // Sugestão de modelagem:
        // O local está sendo salvo tanto em AlocacaoEntity quanto em CompeticaoEntity.
        // É importante verificar se essa duplicidade é realmente necessária no modelo.
        //
        // Benefícios:
        // - Evita inconsistência entre os dados.
        // - Reduz duplicidade de informação.
        // - Deixa mais claro qual entidade é responsável pela alocação do local.
        //
        // Sugestão:
        // Avaliar se o local da competição deve ficar apenas na AlocacaoEntity ou se faz sentido também
        // manter essa informação diretamente na CompeticaoEntity.

        // Sugestão de regra de negócio:
        // Antes de salvar a alocação, poderia haver validações adicionais.
        // Por exemplo: verificar se o local já está ocupado ou se a competição já possui uma alocação ativa.
        //
        // Benefícios:
        // - Evita conflitos de agenda ou uso duplicado do mesmo local.
        // - Centraliza regras importantes dentro do caso de uso.
        // - Aumenta a confiabilidade da operação.
        //
        // Sugestão:
        // Criar métodos de validação antes da persistência, como validarDisponibilidadeDoLocal(local, competicao).

        alocacaoRepository.save(alocacao);
        competicaoRepository.save(competicao);

        // Ponto positivo:
        // O uso de @Transactional neste método é adequado, pois existem duas operações de escrita no banco.
        // Assim, caso uma delas falhe, a transação pode ser revertida e os dados continuam consistentes.

        return alocacao.getId();
    }
}
