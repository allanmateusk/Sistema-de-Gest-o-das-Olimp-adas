import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { Layout } from "./components/Layout";
import { RequireAdmin } from "./components/RequireAdmin";
import { RequireAuth } from "./components/RequireAuth";
import { LoginPage } from "./pages/LoginPage";
import { HomePage } from "./pages/HomePage";
import { CompeticoesPage } from "./pages/CompeticoesPage";
import { InscricoesPage } from "./pages/InscricoesPage";
import { AlocacoesPage } from "./pages/AlocacoesPage";
import { ResultadosPage } from "./pages/ResultadosPage";
import { MedalhasPage } from "./pages/MedalhasPage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<RequireAuth />}>
            <Route element={<Layout />}>
              <Route path="/" element={<HomePage />} />
              <Route path="/medalhas" element={<MedalhasPage />} />
              <Route element={<RequireAdmin />}>
                <Route path="/competicoes" element={<CompeticoesPage />} />
                <Route path="/inscricoes" element={<InscricoesPage />} />
                <Route path="/alocacoes" element={<AlocacoesPage />} />
                <Route path="/resultados" element={<ResultadosPage />} />
              </Route>
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
