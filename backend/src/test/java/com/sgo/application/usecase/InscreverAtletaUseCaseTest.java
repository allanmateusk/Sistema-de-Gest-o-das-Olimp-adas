package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.InscricaoEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.InscricaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscreverAtletaUseCaseTest {

    @Mock
    AtletaRepository atletaRepository;
    @Mock
    CompeticaoRepository competicaoRepository;
    @Mock
    InscricaoRepository inscricaoRepository;

    @InjectMocks
    InscreverAtletaUseCase useCase;

    @Test
    void lancaQuandoAtletaNaoExiste() {
        UUID aid = UUID.randomUUID();
        when(atletaRepository.findById(aid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(aid, UUID.randomUUID()));
    }

    @Test
    void lancaQuandoCompeticaoNaoExiste() {
        UUID aid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        var a = new AtletaEntity();
        a.setId(aid);
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(a));
        when(competicaoRepository.findById(cid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(aid, cid));
    }

    @Test
    void lancaQuandoJaInscrito() {
        UUID aid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        var a = new AtletaEntity();
        a.setId(aid);
        var c = new CompeticaoEntity();
        c.setId(cid);
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(a));
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(c));
        when(inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(aid, cid)).thenReturn(true);
        assertThrows(BusinessException.class, () -> useCase.execute(aid, cid));
    }

    @Test
    void inscreveComSucesso() {
        UUID aid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        var a = new AtletaEntity();
        a.setId(aid);
        var c = new CompeticaoEntity();
        c.setId(cid);
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(a));
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(c));
        when(inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(aid, cid)).thenReturn(false);
        when(inscricaoRepository.save(any(InscricaoEntity.class))).thenAnswer(i -> i.getArgument(0));

        UUID inscId = useCase.execute(aid, cid);

        assertNotNull(inscId);
        ArgumentCaptor<InscricaoEntity> cap = ArgumentCaptor.forClass(InscricaoEntity.class);
        verify(inscricaoRepository).save(cap.capture());
        assertEquals(a, cap.getValue().getAtleta());
        assertEquals(c, cap.getValue().getCompeticao());
    }
}
