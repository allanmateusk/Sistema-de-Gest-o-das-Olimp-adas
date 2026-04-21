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
        return relatorioDao.medalhasPorPais();
    }
}
