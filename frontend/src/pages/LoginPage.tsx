import { type FormEvent, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { isAxiosError } from "axios";
import { OlympicRings } from "../components/OlympicRings";
import { useAuth } from "../context/useAuth";

function extractApiErrorMessage(data: unknown): string | null {
  if (data == null || typeof data !== "object") return null;
  const o = data as { detail?: string; title?: string; message?: string };
  return o.detail ?? o.message ?? o.title ?? null;
}

export function LoginPage() {
  const { token, login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  /**
   * readOnly no primeiro render evita o browser a injetar credenciais
   * guardadas antes de o utilizador interagir. Remove-se ao receber foco.
   */
  const [revelarCampos, setRevelarCampos] = useState(false);

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
    <div className="login-page">
      <div className="login-backdrop" aria-hidden />
      <div className="login-content">
        <div className="login-hero">
          <OlympicRings />
          <h1>SGO</h1>
          <p className="sub">
            Acesso reservado a utilizadores autorizados. O token de sessão é armazenado no sessionStorage
            (menos persistente do que localStorage). Utilize as credenciais fornecidas para o ambiente
            (em desenvolvimento com seed, consulte a documentação do repositório).
          </p>
        </div>
        <div className="card narrow login-card">
          <h2>Entrar</h2>
          <form
            onSubmit={onSubmit}
            className="form"
            autoComplete="off"
            method="post"
          >
            <label htmlFor="sgo-login-email">
              Email
              <input
                id="sgo-login-email"
                name="sgo_credential_email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                onFocus={() => setRevelarCampos(true)}
                readOnly={!revelarCampos}
                autoComplete="off"
                data-1p-ignore
                data-lpignore="true"
                data-bwignore
                placeholder="nome@organizacao.tld"
                required
              />
            </label>
            <label htmlFor="sgo-login-senha">
              Senha
              <input
                id="sgo-login-senha"
                name="sgo_credential_senha"
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                onFocus={() => setRevelarCampos(true)}
                readOnly={!revelarCampos}
                autoComplete="off"
                data-1p-ignore
                data-lpignore="true"
                data-bwignore
                placeholder="••••••••"
                required
              />
            </label>
            {erro && <p className="error">{erro}</p>}
            <button type="submit" className="btn primary" disabled={loading}>
              {loading ? "Entrando…" : "Entrar"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
