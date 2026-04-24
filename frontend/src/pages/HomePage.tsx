import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

export function HomePage() {
  const { isAdmin, perfil } = useAuth();

  return (
    <div className="stack">
      <div className="page-hero">
        <h1 className="page-title">Bem-vindo</h1>
        <p>
          Perfil: <strong>{perfil === "ADMIN" ? "Admin" : "Usuário"}</strong> —{" "}
          acompanhe competições, resultados e pódio. Rotas de cadastro e resultados
          requerem perfil de administrador.
        </p>
      </div>
      <div className="grid-links">
        {isAdmin && (
          <>
            <Link className="tile sport-c1" to="/competicoes">
              <h3>Competições</h3>
              <p>Cadastrar e listar provas, datas e locais</p>
            </Link>
            <Link className="tile sport-c2" to="/inscricoes">
              <h3>Inscrições</h3>
              <p>Inscrever atletas nas competições</p>
            </Link>
            <Link className="tile sport-c3" to="/alocacoes">
              <h3>Alocações</h3>
              <p>Definir pistas e centros de competição</p>
            </Link>
            <Link className="tile sport-c4" to="/resultados">
              <h3>Resultados</h3>
              <p>Registrar posições e pódio</p>
            </Link>
          </>
        )}
        <Link className="tile sport-c5" to="/medalhas">
          <h3>Medalhas</h3>
          <p>Contagem de ouro, prata e bronze por país</p>
        </Link>
      </div>
    </div>
  );
}
