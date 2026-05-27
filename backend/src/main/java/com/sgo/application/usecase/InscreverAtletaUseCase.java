package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.InscricaoEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.InscricaoRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class InscreverAtletaUseCase {

    private final AtletaRepository atletaRepository;
    private final CompeticaoRepository competicaoRepository;
    private final InscricaoRepository inscricaoRepository;

    public InscreverAtletaUseCase(
            AtletaRepository atletaRepository,
            CompeticaoRepository competicaoRepository,
            InscricaoRepository inscricaoRepository
    ) {
        this.atletaRepository = atletaRepository;
        this.competicaoRepository = competicaoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    @Transactional
    public UUID execute(UUID atletaId, UUID competicaoId) {

        // Seria importante validar se atletaId e competicaoId são nulos antes de fazer a busca.
        // Assim o sistema evita erro inesperado e consegue retornar uma mensagem mais clara.

        AtletaEntity atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new NotFoundException("Atleta não encontrado"));

        CompeticaoEntity competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new NotFoundException("Competição não encontrada"));

        if (inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(atletaId, competicaoId)) {
            throw new BusinessException("Atleta já inscrito nesta competição");
        }

        // A validação para não deixar o atleta se inscrever duas vezes está bem colocada.
        // Isso evita duplicidade no banco e mantém a regra de negócio dentro do caso de uso.

        InscricaoEntity inscricao = new InscricaoEntity();
        inscricao.setId(UUID.randomUUID());
        inscricao.setAtleta(atleta);
        inscricao.setCompeticao(competicao);

        // Se a criação da inscrição ganhar mais campos depois, pode ser melhor separar essa parte em um método privado.
        // Isso deixaria o execute mais curto e mais fácil de ler.

        inscricaoRepository.save(inscricao);

        return inscricao.getId();
    }
}
