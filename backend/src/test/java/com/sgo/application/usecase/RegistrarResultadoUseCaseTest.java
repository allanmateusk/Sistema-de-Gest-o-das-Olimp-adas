package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.ResultadoEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.InscricaoRepository;
import com.sgo.infrastructure.persistence.repository.ResultadoRepository;
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
class RegistrarResultadoUseCaseTest {

    @Mock
    CompeticaoRepository competicaoRepository;
    @Mock
    AtletaRepository atletaRepository;
    @Mock
    InscricaoRepository inscricaoRepository;
    @Mock
    ResultadoRepository resultadoRepository;

    @InjectMocks
    RegistrarResultadoUseCase useCase;

    @Test
    void lancaQuandoPosicaoNula() {
        assertThrows(BusinessException.class, () -> useCase.execute(UUID.randomUUID(), UUID.randomUUID(), null));
    }

    @Test
    void lancaQuandoPosicaoInvalida() {
        assertThrows(BusinessException.class, () -> useCase.execute(UUID.randomUUID(), UUID.randomUUID(), 0));
    }

    @Test
    void lancaQuandoCompeticaoNaoExiste() {
        UUID cid = UUID.randomUUID();
        when(competicaoRepository.findById(cid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(cid, UUID.randomUUID(), 1));
    }

    @Test
    void lancaQuandoAtletaNaoExiste() {
        UUID cid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(new CompeticaoEntity()));
        when(atletaRepository.findById(aid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(cid, aid, 1));
    }

    @Test
    void lancaQuandoNaoInscrito() {
        UUID cid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(new CompeticaoEntity()));
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(new AtletaEntity()));
        when(inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(aid, cid)).thenReturn(false);
        assertThrows(BusinessException.class, () -> useCase.execute(cid, aid, 1));
    }

    @Test
    void lancaQuandoResultadoJaExisteParaOAtleta() {
        UUID cid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(new CompeticaoEntity()));
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(new AtletaEntity()));
        when(inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(aid, cid)).thenReturn(true);
        when(resultadoRepository.findByCompeticao_IdAndAtleta_Id(cid, aid))
                .thenReturn(Optional.of(new ResultadoEntity()));
        assertThrows(BusinessException.class, () -> useCase.execute(cid, aid, 1));
    }

    @Test
    void lancaQuandoPosicaoOcupada() {
        UUID cid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(new CompeticaoEntity()));
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(new AtletaEntity()));
        when(inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(aid, cid)).thenReturn(true);
        when(resultadoRepository.findByCompeticao_IdAndAtleta_Id(cid, aid)).thenReturn(Optional.empty());
        when(resultadoRepository.findByCompeticao_IdAndPosicao(cid, 2)).thenReturn(Optional.of(new ResultadoEntity()));
        assertThrows(BusinessException.class, () -> useCase.execute(cid, aid, 2));
    }

    @Test
    void registraComSucesso() {
        UUID cid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        var c = new CompeticaoEntity();
        c.setId(cid);
        var a = new AtletaEntity();
        a.setId(aid);
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(c));
        when(atletaRepository.findById(aid)).thenReturn(Optional.of(a));
        when(inscricaoRepository.existsByAtleta_IdAndCompeticao_Id(aid, cid)).thenReturn(true);
        when(resultadoRepository.findByCompeticao_IdAndAtleta_Id(cid, aid)).thenReturn(Optional.empty());
        when(resultadoRepository.findByCompeticao_IdAndPosicao(cid, 3)).thenReturn(Optional.empty());
        when(resultadoRepository.save(any(ResultadoEntity.class))).thenAnswer(i -> i.getArgument(0));

        UUID resId = useCase.execute(cid, aid, 3);

        assertNotNull(resId);
        ArgumentCaptor<ResultadoEntity> cap = ArgumentCaptor.forClass(ResultadoEntity.class);
        verify(resultadoRepository).save(cap.capture());
        assertEquals(Integer.valueOf(3), cap.getValue().getPosicao());
        assertEquals(c, cap.getValue().getCompeticao());
        assertEquals(a, cap.getValue().getAtleta());
    }
}
