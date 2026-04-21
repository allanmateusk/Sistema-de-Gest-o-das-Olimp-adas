package com.sgo.infrastructure.persistence;

import com.sgo.application.dto.MedalhaPorPaisDto;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class RelatorioDao {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<MedalhaPorPaisDto> medalhasPorPais() {
        String sql = """
                SELECT p.id AS pais_id,
                       p.nome AS pais_nome,
                       COALESCE(SUM(CASE WHEN r.posicao = 1 THEN 1 ELSE 0 END), 0) AS ouro,
                       COALESCE(SUM(CASE WHEN r.posicao = 2 THEN 1 ELSE 0 END), 0) AS prata,
                       COALESCE(SUM(CASE WHEN r.posicao = 3 THEN 1 ELSE 0 END), 0) AS bronze
                FROM resultados r
                         JOIN atletas a ON a.id = r.atleta_id
                         JOIN paises p ON p.id = a.pais_id
                GROUP BY p.id, p.nome
                ORDER BY ouro DESC, prata DESC, bronze DESC, p.nome ASC
                """;
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> rows = query.getResultList();
        List<MedalhaPorPaisDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            UUID paisId = row[0] instanceof UUID u ? u : UUID.fromString(row[0].toString());
            String nome = (String) row[1];
            long ouro = toLong(row[2]);
            long prata = toLong(row[3]);
            long bronze = toLong(row[4]);
            result.add(new MedalhaPorPaisDto(paisId, nome, ouro, prata, bronze));
        }
        return result;
    }

    private static long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(value.toString());
    }
}
