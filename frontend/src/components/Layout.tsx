import { Link, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function Layout() {
  const { logout, isAdmin, perfil } = useAuth();

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="logo">SGO</span>
          <span className="muted">Gestão das Olimpíadas</span>
        </div>
        <nav className="nav">
          <Link to="/">Início</Link>
          {isAdmin && <Link to="/competicoes">Competições</Link>}
          {isAdmin && <Link to="/inscricoes">Inscrições</Link>}
          {isAdmin && <Link to="/alocacoes">Alocações</Link>}
          {isAdmin && <Link to="/resultados">Resultados</Link>}
          <Link to="/medalhas">Medalhas</Link>
        </nav>
        <div className="user">
          <span className="muted">{perfil ?? "—"}</span>
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
