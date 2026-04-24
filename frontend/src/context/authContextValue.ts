import { createContext } from "react";
import type { Perfil } from "../types";

export type AuthState = {
  token: string | null;
  perfil: Perfil | null;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  isAdmin: boolean;
};

export const AuthContext = createContext<AuthState | undefined>(undefined);
