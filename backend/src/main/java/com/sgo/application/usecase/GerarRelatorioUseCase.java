package com.sgo.application.usecase;

import com.sgo.application.dto.MedalhaPorPaisDto;
import com.sgo.infrastructure.persistence.RelatorioDao;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

@Singleton
public class GerarRelatorioUseCase {

    private final RelatorioDao relatorioDao;

    public GerarRelatorioUseCase(RelatorioDao relatorioDao) {
        this.relatorioDao = relatorioDao;
    }

    @Transactional
    public List<MedalhaPorPaisDto> execute() {

        // Como esse método apenas busca dados para gerar um relatório, talvez o @Transactional não seja necessário.
        // Se não tiver nenhuma alteração no banco, o método pode ficar sem essa anotação para deixar mais claro que é só uma consulta.

        return relatorioDao.medalhasPorPais();
    }
}
