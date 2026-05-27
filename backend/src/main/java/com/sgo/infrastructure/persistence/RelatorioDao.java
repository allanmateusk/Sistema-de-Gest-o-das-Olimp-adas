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

        // A consulta está bem clara para gerar o quadro de medalhas por país.
        // Como ela é uma query nativa, seria bom deixar um comentário curto explicando o motivo de não usar JPQL.
        // Isso ajuda quem for manter o código depois a entender a escolha.

        Query query = entityManager.createNativeQuery(sql);

        // O uso de query nativa retorna Object[], então o código precisa fazer conversões manuais.
        // Uma melhoria seria usar uma projeção ou mapeamento específico, para reduzir o risco de erro na ordem das colunas.

        List<Object[]> rows = query.getResultList();
        List<MedalhaPorPaisDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            UUID paisId = row[0] instanceof UUID u ? u : UUID.fromString(row[0].toString());
            String nome = (String) row[1];
            long ouro = toLong(row[2]);
            long prata = toLong(row[3]);
            long bronze = toLong(row[4]);

            // Essa conversão manual funciona, mas depende muito da posição correta das colunas no SELECT.
            // Se alguém alterar a query depois, pode acabar quebrando o mapeamento sem perceber.
            // Separar essa parte em um método, como toDto(row), deixaria o loop mais limpo.

            result.add(new MedalhaPorPaisDto(paisId, nome, ouro, prata, bronze));
        }

        return result;
    }

    private static long toLong(Object value) {

        // Esse método é útil porque trata diferentes tipos que podem vir do banco.
        // Isso evita repetir conversões no meio do método principal.

        if (value == null) {
            return 0L;
        }

        if (value instanceof Number n) {
            return n.longValue();
        }

        return Long.parseLong(value.toString());
    }
}
