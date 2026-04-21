import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function HomePage() {
  const { isAdmin, perfil } = useAuth();

  return (
    <div className="stack">
      <h1>Painel</h1>
      <p>
        Perfil atual: <strong>{perfil}</strong>
      </p>
      <p className="muted">
        Use o menu para navegar. Rotas administrativas exigem perfil ADMIN.
      </p>
      <div className="grid-links">
        {isAdmin && (
          <>
            <Link className="tile" to="/competicoes">
              <h3>Competições</h3>
              <p>Cadastrar e listar competições</p>
            </Link>
            <Link className="tile" to="/inscricoes">
              <h3>Inscrições</h3>
              <p>Inscrever atletas</p>
            </Link>
            <Link className="tile" to="/alocacoes">
              <h3>Alocações</h3>
              <p>Definir local da competição</p>
            </Link>
            <Link className="tile" to="/resultados">
              <h3>Resultados</h3>
              <p>Registrar posições</p>
            </Link>
          </>
        )}
        <Link className="tile" to="/medalhas">
          <h3>Medalhas</h3>
          <p>Relatório por país</p>
        </Link>
      </div>
    </div>
  );
}
