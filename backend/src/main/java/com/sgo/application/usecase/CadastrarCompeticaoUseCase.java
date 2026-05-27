package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@Singleton
public class CadastrarCompeticaoUseCase {

    private final CompeticaoRepository competicaoRepository;

    public CadastrarCompeticaoUseCase(CompeticaoRepository competicaoRepository) {
        this.competicaoRepository = competicaoRepository;
    }

    @Transactional
    public UUID execute(String nome, String modalidade, Instant dataInicio, Instant dataFim) {

        // Antes de comparar as datas, seria importante validar se dataInicio e dataFim são nulas.
        // Do jeito que está, se alguma delas vier nula, o sistema pode gerar um NullPointerException.
        // Uma validação simples no começo do método deixaria o erro mais claro para quem estiver usando a aplicação.

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("dataInicio não pode ser posterior a dataFim");
        }

        // Também seria interessante validar os campos nome e modalidade.
        // Esses campos parecem ser obrigatórios para cadastrar uma competição, então não deveriam aceitar valor nulo ou texto vazio.
        // Isso evita salvar uma competição incompleta no banco.

        CompeticaoEntity e = new CompeticaoEntity();

        // O nome da variável "e" funciona, mas não deixa muito claro o que ela representa.
        // Usar um nome como "competicao" deixaria o código mais fácil de entender, principalmente para quem for dar manutenção depois.

        e.setId(UUID.randomUUID());
        e.setNome(nome);
        e.setModalidade(modalidade);
        e.setDataInicio(dataInicio);
        e.setDataFim(dataFim);

        // A criação da entidade está toda dentro do método execute.
        // Como ainda é um trecho pequeno, não atrapalha muito, mas se no futuro surgirem mais campos ou regras,
        // pode ser melhor separar essa montagem em um método privado, como criarCompeticao(...).

        competicaoRepository.save(e);

        // O uso do @Transactional faz sentido aqui, porque o método está salvando uma nova competição no banco.
        // Assim, se acontecer algum erro durante o cadastro, a operação pode ser desfeita corretamente.

        return e.getId();
    }
}
