import { type FormEvent, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { isAxiosError } from "axios";

function extractApiErrorMessage(data: unknown): string | null {
  if (data == null || typeof data !== "object") return null;
  const o = data as { detail?: string; title?: string; message?: string };
  return o.detail ?? o.message ?? o.title ?? null;
}

export function LoginPage() {
  const { token, login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("admin@sgo.local");
  const [senha, setSenha] = useState("Admin@123");
  const [erro, setErro] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (token) return <Navigate to="/" replace />;

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setLoading(true);
    try {
      await login(email, senha);
      navigate("/", { replace: true });
    } catch (err: unknown) {
      const msg = isAxiosError(err)
        ? extractApiErrorMessage(err.response?.data) ?? err.message
        : "Falha no login";
      setErro(msg);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card narrow">
      <h1>Entrar</h1>
      <p className="muted">
        Token JWT em <code>sessionStorage</code> (mitiga persistência em disco).
      </p>
      <form onSubmit={onSubmit} className="form">
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="username"
            required
          />
        </label>
        <label>
          Senha
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>
        {erro && <p className="error">{erro}</p>}
        <button type="submit" className="btn primary" disabled={loading}>
          {loading ? "Entrando…" : "Entrar"}
        </button>
      </form>
    </div>
  );
}
