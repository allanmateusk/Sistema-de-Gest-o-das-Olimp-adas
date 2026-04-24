package com.sgo.application.usecase;

import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCompeticoesUseCaseTest {

    @Mock
    CompeticaoRepository competicaoRepository;

    @InjectMocks
    ListarCompeticoesUseCase useCase;

    @Test
    void listaVazia() {
        when(competicaoRepository.findAll()).thenReturn(List.of());
        assertTrue(useCase.execute().isEmpty());
    }

    @Test
    void mapeiaLocalIdQuandoPreenchido() {
        Instant ini = Instant.parse("2026-01-01T00:00:00Z");
        Instant fim = Instant.parse("2026-01-10T00:00:00Z");
        UUID cid = UUID.randomUUID();
        UUID lid = UUID.randomUUID();
        var local = new LocalEntity();
        local.setId(lid);
        var e = new CompeticaoEntity();
        e.setId(cid);
        e.setNome("Judo");
        e.setModalidade("luta");
        e.setDataInicio(ini);
        e.setDataFim(fim);
        e.setLocal(local);
        when(competicaoRepository.findAll()).thenReturn(List.of(e));

        var row = useCase.execute().getFirst();
        assertEquals(lid, row.localId());
        assertEquals(ini, row.dataInicio());
    }

    @Test
    void mapeiaLocalIdNulo() {
        Instant ini = Instant.parse("2026-01-01T00:00:00Z");
        Instant fim = Instant.parse("2026-01-10T00:00:00Z");
        var e = new CompeticaoEntity();
        e.setId(UUID.randomUUID());
        e.setNome("X");
        e.setDataInicio(ini);
        e.setDataFim(fim);
        e.setLocal(null);
        when(competicaoRepository.findAll()).thenReturn(List.of(e));

        assertNull(useCase.execute().getFirst().localId());
        verify(competicaoRepository).findAll();
    }
}
