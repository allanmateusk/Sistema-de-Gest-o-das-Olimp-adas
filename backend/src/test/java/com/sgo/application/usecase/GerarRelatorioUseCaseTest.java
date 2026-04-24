package com.sgo.application.usecase;

import com.sgo.application.dto.MedalhaPorPaisDto;
import com.sgo.infrastructure.persistence.RelatorioDao;
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
class GerarRelatorioUseCaseTest {

    @Mock
    RelatorioDao relatorioDao;

    @InjectMocks
    GerarRelatorioUseCase useCase;

    @Test
    void retornaDadosDoDao() {
        UUID paisId = UUID.randomUUID();
        var linhas = List.of(new MedalhaPorPaisDto(paisId, "Brasil", 2, 1, 0));
        when(relatorioDao.medalhasPorPais()).thenReturn(linhas);

        List<MedalhaPorPaisDto> out = useCase.execute();

        assertEquals(1, out.size());
        assertEquals(2, out.getFirst().ouro());
        verify(relatorioDao).medalhasPorPais();
    }

    @Test
    void retornaVazio() {
        when(relatorioDao.medalhasPorPais()).thenReturn(List.of());

        assertTrue(useCase.execute().isEmpty());
    }
}
