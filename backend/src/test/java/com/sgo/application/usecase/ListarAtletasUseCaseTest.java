package com.sgo.application.usecase;

import com.sgo.infrastructure.persistence.entity.AtletaEntity;
import com.sgo.infrastructure.persistence.entity.PaisEntity;
import com.sgo.infrastructure.persistence.repository.AtletaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarAtletasUseCaseTest {

    @Mock
    AtletaRepository atletaRepository;

    @InjectMocks
    ListarAtletasUseCase useCase;

    @Test
    void listaVazia() {
        when(atletaRepository.findAll()).thenReturn(List.of());
        assertTrue(useCase.execute().isEmpty());
    }

    @Test
    void mapeiaPais() {
        UUID aid = UUID.randomUUID();
        UUID pid = UUID.randomUUID();
        var p = new PaisEntity();
        p.setId(pid);
        p.setNome("Brasil");
        var a = new AtletaEntity();
        a.setId(aid);
        a.setNome("Maria");
        a.setPais(p);
        when(atletaRepository.findAll()).thenReturn(List.of(a));

        var row = useCase.execute().getFirst();
        assertEquals(aid, row.id());
        assertEquals(pid, row.paisId());
        assertEquals("Brasil", row.paisNome());
        verify(atletaRepository).findAll();
    }
}
