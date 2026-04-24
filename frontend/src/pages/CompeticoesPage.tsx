import { type FormEvent, useEffect, useState } from "react";
import { api } from "../api";
import type { Competicao, IdResponse } from "../types";
import { isAxiosError } from "axios";

export function CompeticoesPage() {
  const [lista, setLista] = useState<Competicao[]>([]);
  const [nome, setNome] = useState("");
  const [modalidade, setModalidade] = useState("");
  const [inicio, setInicio] = useState("");
  const [fim, setFim] = useState("");
  const [msg, setMsg] = useState<string | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  async function carregar() {
    const { data } = await api.get<Competicao[]>("/competicoes");
    setLista(data);
  }

  useEffect(() => {
    let active = true;
    void (async () => {
      try {
        const { data } = await api.get<Competicao[]>("/competicoes");
        if (active) setLista(data);
      } catch {
        if (active) setErro("Não foi possível carregar competições");
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setMsg(null);
    try {
      const { data } = await api.post<IdResponse>("/competicoes", {
        nome,
        modalidade,
        dataInicio: inicio,
        dataFim: fim,
      });
      setMsg(`Competição criada: ${data.id}`);
      await carregar();
    } catch (err: unknown) {
      setErro(
        isAxiosError(err)
          ? (err.response?.data as { detail?: string })?.detail ?? err.message
          : "Erro ao criar"
      );
    }
  }

  return (
    <div className="stack">
      <h1>Competições</h1>
      <div className="card">
        <h2>Nova competição</h2>
        <form onSubmit={onSubmit} className="form">
          <label>
            Nome
            <input
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Ex.: 100m rasos"
              required
            />
          </label>
          <label>
            Modalidade
            <input
              value={modalidade}
              onChange={(e) => setModalidade(e.target.value)}
              placeholder="Ex.: Atletismo"
            />
          </label>
          <label>
            Início (ISO-8601)
            <input
              value={inicio}
              onChange={(e) => setInicio(e.target.value)}
              placeholder="2026-07-01T10:00:00.000Z"
              required
            />
          </label>
          <label>
            Fim (ISO-8601)
            <input
              value={fim}
              onChange={(e) => setFim(e.target.value)}
              placeholder="2026-07-15T18:00:00.000Z"
              required
            />
          </label>
          {erro && <p className="error">{erro}</p>}
          {msg && <p className="success">{msg}</p>}
          <button type="submit" className="btn primary">
            Cadastrar
          </button>
        </form>
      </div>
      <div className="card">
        <h2>Lista</h2>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Início</th>
              <th>Fim</th>
            </tr>
          </thead>
          <tbody>
            {lista.map((c) => (
              <tr key={c.id}>
                <td className="mono">{c.id}</td>
                <td>{c.nome}</td>
                <td>{c.dataInicio}</td>
                <td>{c.dataFim}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
