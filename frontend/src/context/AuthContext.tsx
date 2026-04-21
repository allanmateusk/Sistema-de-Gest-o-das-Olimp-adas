import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { api } from "../api";
import type { LoginResponse, Perfil } from "../types";

type AuthState = {
  token: string | null;
  perfil: Perfil | null;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  isAdmin: boolean;
};

const AuthContext = createContext<AuthState | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    sessionStorage.getItem("sgo_token")
  );
  const [perfil, setPerfil] = useState<Perfil | null>(() =>
    (sessionStorage.getItem("sgo_perfil") as Perfil | null) ?? null
  );

  const login = useCallback(async (email: string, senha: string) => {
    const { data } = await api.post<LoginResponse>("/auth/login", { email, senha });
    sessionStorage.setItem("sgo_token", data.accessToken);
    sessionStorage.setItem("sgo_perfil", data.perfil);
    setToken(data.accessToken);
    setPerfil(data.perfil);
  }, []);

  const logout = useCallback(() => {
    sessionStorage.removeItem("sgo_token");
    sessionStorage.removeItem("sgo_perfil");
    setToken(null);
    setPerfil(null);
  }, []);

  const value = useMemo<AuthState>(
    () => ({
      token,
      perfil,
      login,
      logout,
      isAdmin: perfil === "ADMIN",
    }),
    [token, perfil, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth fora de AuthProvider");
  return ctx;
}
