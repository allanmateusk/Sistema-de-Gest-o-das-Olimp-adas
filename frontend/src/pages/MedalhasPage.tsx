import { useEffect, useState } from "react";
import { api } from "../api";
import type { MedalhaPorPais } from "../types";

export function MedalhasPage() {
  const [dados, setDados] = useState<MedalhaPorPais[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get<MedalhaPorPais[]>("/relatorios/medalhas");
        setDados(data);
      } catch {
        setErro("Não foi possível carregar o relatório");
      }
    })();
  }, []);

  return (
    <div className="stack">
      <h1>Relatório de medalhas</h1>
      {erro && <p className="error">{erro}</p>}
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>País</th>
              <th>Ouro</th>
              <th>Prata</th>
              <th>Bronze</th>
            </tr>
          </thead>
          <tbody>
            {dados.map((m) => (
              <tr key={m.paisId}>
                <td>{m.paisNome}</td>
                <td>{m.ouro}</td>
                <td>{m.prata}</td>
                <td>{m.bronze}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {dados.length === 0 && !erro && <p className="muted">Sem dados ainda.</p>}
      </div>
    </div>
  );
}
