package com.sgo.application.usecase;

import com.sgo.domain.exception.NotFoundException;
import com.sgo.infrastructure.persistence.entity.AlocacaoEntity;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.repository.AlocacaoRepository;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import com.sgo.infrastructure.persistence.repository.LocalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AlocarLocalUseCaseTest {

    @Mock
    CompeticaoRepository competicaoRepository;
    @Mock
    LocalRepository localRepository;
    @Mock
    AlocacaoRepository alocacaoRepository;

    @InjectMocks
    AlocarLocalUseCase useCase;

    @Test
    void lancaQuandoCompeticaoNaoExiste() {
        UUID cid = UUID.randomUUID();
        when(competicaoRepository.findById(cid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(cid, UUID.randomUUID()));
    }

    @Test
    void lancaQuandoLocalNaoExiste() {
        UUID cid = UUID.randomUUID();
        UUID lid = UUID.randomUUID();
        var c = new CompeticaoEntity();
        c.setId(cid);
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(c));
        when(localRepository.findById(lid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(cid, lid));
    }

    @Test
    void criaAlocacaoQuandoNaoHavia() {
        UUID cid = UUID.randomUUID();
        UUID lid = UUID.randomUUID();
        var c = new CompeticaoEntity();
        c.setId(cid);
        var l = new LocalEntity();
        l.setId(lid);
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(c));
        when(localRepository.findById(lid)).thenReturn(Optional.of(l));
        when(alocacaoRepository.findByCompeticao_Id(cid)).thenReturn(Optional.empty());
        when(alocacaoRepository.save(any(AlocacaoEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(competicaoRepository.save(any(CompeticaoEntity.class))).thenAnswer(i -> i.getArgument(0));

        UUID alocId = useCase.execute(cid, lid);

        assertNotNull(alocId);
        verify(competicaoRepository).save(c);
        assertEquals(l, c.getLocal());
    }

    @Test
    void atualizaAlocacaoExistente() {
        UUID cid = UUID.randomUUID();
        UUID lid = UUID.randomUUID();
        var c = new CompeticaoEntity();
        c.setId(cid);
        var l = new LocalEntity();
        l.setId(lid);
        UUID existingId = UUID.randomUUID();
        var aloc = new AlocacaoEntity();
        aloc.setId(existingId);
        aloc.setCompeticao(c);
        when(competicaoRepository.findById(cid)).thenReturn(Optional.of(c));
        when(localRepository.findById(lid)).thenReturn(Optional.of(l));
        when(alocacaoRepository.findByCompeticao_Id(cid)).thenReturn(Optional.of(aloc));
        when(alocacaoRepository.save(any(AlocacaoEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(competicaoRepository.save(any(CompeticaoEntity.class))).thenAnswer(i -> i.getArgument(0));

        UUID alocId = useCase.execute(cid, lid);

        assertEquals(existingId, alocId);
        assertEquals(l, aloc.getLocal());
        assertEquals(l, c.getLocal());
    }
}
