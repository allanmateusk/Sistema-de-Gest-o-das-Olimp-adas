import { type FormEvent, useEffect, useState } from "react";
import { api } from "../api";
import type { Atleta, Competicao, IdResponse } from "../types";
import { isAxiosError } from "axios";

export function ResultadosPage() {
  const [atletas, setAtletas] = useState<Atleta[]>([]);
  const [competicoes, setCompeticoes] = useState<Competicao[]>([]);
  const [atletaId, setAtletaId] = useState("");
  const [competicaoId, setCompeticaoId] = useState("");
  const [posicao, setPosicao] = useState(1);
  const [msg, setMsg] = useState<string | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const [a, c] = await Promise.all([
          api.get<Atleta[]>("/atletas"),
          api.get<Competicao[]>("/competicoes"),
        ]);
        setAtletas(a.data);
        setCompeticoes(c.data);
        if (a.data[0]) setAtletaId(a.data[0].id);
        if (c.data[0]) setCompeticaoId(c.data[0].id);
      } catch {
        setErro("Falha ao carregar listas");
      }
    })();
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setMsg(null);
    try {
      const { data } = await api.post<IdResponse>("/resultados", {
        competicaoId,
        atletaId,
        posicao,
      });
      setMsg(`Resultado registrado: ${data.id}`);
    } catch (err: unknown) {
      setErro(
        isAxiosError(err)
          ? (err.response?.data as { detail?: string })?.detail ?? err.message
          : "Erro"
      );
    }
  }

  return (
    <div className="stack">
      <h1>Resultados</h1>
      <div className="card">
        <form onSubmit={onSubmit} className="form">
          <label>
            Competição
            <select value={competicaoId} onChange={(e) => setCompeticaoId(e.target.value)}>
              {competicoes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nome}
                </option>
              ))}
            </select>
          </label>
          <label>
            Atleta
            <select value={atletaId} onChange={(e) => setAtletaId(e.target.value)}>
              {atletas.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.nome}
                </option>
              ))}
            </select>
          </label>
          <label>
            Posição (1=ouro, 2=prata, 3=bronze nas regras do relatório)
            <input
              type="number"
              min={1}
              value={posicao}
              onChange={(e) => setPosicao(Number(e.target.value))}
            />
          </label>
          {erro && <p className="error">{erro}</p>}
          {msg && <p className="success">{msg}</p>}
          <button type="submit" className="btn primary">
            Registrar
          </button>
        </form>
      </div>
    </div>
  );
}
