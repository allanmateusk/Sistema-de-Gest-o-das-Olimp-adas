package com.sgo.application.usecase;

import com.sgo.domain.exception.BusinessException;
import com.sgo.infrastructure.persistence.entity.CompeticaoEntity;
import com.sgo.infrastructure.persistence.repository.CompeticaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarCompeticaoUseCaseTest {

    @Mock
    CompeticaoRepository competicaoRepository;

    @InjectMocks
    CadastrarCompeticaoUseCase useCase;

    @Test
    void cadastraQuandoDatasValidas() {
        Instant inicio = Instant.parse("2026-07-01T10:00:00Z");
        Instant fim = Instant.parse("2026-07-15T18:00:00Z");
        when(competicaoRepository.save(any(CompeticaoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var id = useCase.execute("100m rasos", "Atletismo", inicio, fim);

        assertNotNull(id);
        ArgumentCaptor<CompeticaoEntity> captor = ArgumentCaptor.forClass(CompeticaoEntity.class);
        verify(competicaoRepository).save(captor.capture());
        assertEquals("100m rasos", captor.getValue().getNome());
    }

    @Test
    void rejeitaQuandoInicioDepoisDoFim() {
        Instant inicio = Instant.parse("2026-07-20T10:00:00Z");
        Instant fim = Instant.parse("2026-07-01T18:00:00Z");

        assertThrows(BusinessException.class, () -> useCase.execute("X", null, inicio, fim));
    }
}
