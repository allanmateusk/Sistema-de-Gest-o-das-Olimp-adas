import { Link, Outlet } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import { OlympicRings } from "./OlympicRings";

export function Layout() {
  const { logout, isAdmin, perfil } = useAuth();

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <OlympicRings />
          <div className="logo-block">
            <span className="logo">SGO</span>
            <span className="tagline">Gestão das olimpíadas</span>
          </div>
        </div>
        <nav className="nav" aria-label="Navegação principal">
          <Link className="nav-link" to="/">
            Início
          </Link>
          {isAdmin && (
            <Link className="nav-link" to="/competicoes">
              Competições
            </Link>
          )}
          {isAdmin && (
            <Link className="nav-link" to="/inscricoes">
              Inscrições
            </Link>
          )}
          {isAdmin && (
            <Link className="nav-link" to="/alocacoes">
              Alocações
            </Link>
          )}
          {isAdmin && (
            <Link className="nav-link" to="/resultados">
              Resultados
            </Link>
          )}
          <Link className="nav-link" to="/medalhas">
            Medalhas
          </Link>
        </nav>
        <div className="user">
          <span className="muted" title="Seu perfil de acesso">
            {perfil === "ADMIN" ? "Admin" : perfil === "USUARIO" ? "Usuário" : perfil ?? "—"}
          </span>
          <button type="button" className="btn ghost" onClick={logout}>
            Sair
          </button>
        </div>
      </header>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
