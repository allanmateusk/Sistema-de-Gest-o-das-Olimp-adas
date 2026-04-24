import { useCallback, useMemo, useState, type ReactNode } from "react";
import { api } from "../api";
import type { LoginResponse } from "../types";
import { AuthContext, type AuthState } from "./authContextValue";

export type { AuthState } from "./authContextValue";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    sessionStorage.getItem("sgo_token")
  );
  const [perfil, setPerfil] = useState<AuthState["perfil"]>(() =>
    (sessionStorage.getItem("sgo_perfil") as AuthState["perfil"]) ?? null
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
