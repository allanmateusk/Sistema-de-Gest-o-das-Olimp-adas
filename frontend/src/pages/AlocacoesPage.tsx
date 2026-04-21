import { type FormEvent, useEffect, useState } from "react";
import { api } from "../api";
import type { Competicao, IdResponse, Local } from "../types";
import { isAxiosError } from "axios";

export function AlocacoesPage() {
  const [locais, setLocais] = useState<Local[]>([]);
  const [competicoes, setCompeticoes] = useState<Competicao[]>([]);
  const [competicaoId, setCompeticaoId] = useState("");
  const [localId, setLocalId] = useState("");
  const [msg, setMsg] = useState<string | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const [l, c] = await Promise.all([
          api.get<Local[]>("/locais"),
          api.get<Competicao[]>("/competicoes"),
        ]);
        setLocais(l.data);
        setCompeticoes(c.data);
        if (c.data[0]) setCompeticaoId(c.data[0].id);
        if (l.data[0]) setLocalId(l.data[0].id);
      } catch {
        setErro("Falha ao carregar dados");
      }
    })();
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setMsg(null);
    try {
      const { data } = await api.post<IdResponse>("/alocacoes", { competicaoId, localId });
      setMsg(`Alocação salva: ${data.id}`);
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
      <h1>Alocações</h1>
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
            Local
            <select value={localId} onChange={(e) => setLocalId(e.target.value)}>
              {locais.map((l) => (
                <option key={l.id} value={l.id}>
                  {l.nome} — {l.cidade}
                </option>
              ))}
            </select>
          </label>
          {erro && <p className="error">{erro}</p>}
          {msg && <p className="success">{msg}</p>}
          <button type="submit" className="btn primary">
            Alocar
          </button>
        </form>
      </div>
    </div>
  );
}
