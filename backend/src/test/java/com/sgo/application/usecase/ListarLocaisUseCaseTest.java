package com.sgo.application.usecase;

import com.sgo.infrastructure.persistence.entity.LocalEntity;
import com.sgo.infrastructure.persistence.repository.LocalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarLocaisUseCaseTest {

    @Mock
    LocalRepository localRepository;

    @InjectMocks
    ListarLocaisUseCase useCase;

    @Test
    void listaVazia() {
        when(localRepository.findAll()).thenReturn(List.of());
        assertTrue(useCase.execute().isEmpty());
    }

    @Test
    void mapeiaEntidades() {
        UUID id = UUID.randomUUID();
        var l = new LocalEntity();
        l.setId(id);
        l.setNome("Arena 1");
        l.setCidade("São Paulo");
        l.setCapacidade(5000);
        when(localRepository.findAll()).thenReturn(List.of(l));
        var row = useCase.execute().getFirst();
        assertEquals(id, row.id());
        assertEquals("São Paulo", row.cidade());
        assertEquals(5000, row.capacidade());
    }
}
