export type Perfil = "ADMIN" | "USUARIO";

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  perfil: Perfil;
}

export interface IdResponse {
  id: string;
}

export interface Competicao {
  id: string;
  nome: string;
  modalidade: string | null;
  dataInicio: string;
  dataFim: string;
  localId: string | null;
}

export interface Local {
  id: string;
  nome: string;
  cidade: string;
  capacidade: number | null;
}

export interface Atleta {
  id: string;
  nome: string;
  paisId: string;
  paisNome: string;
}

export interface MedalhaPorPais {
  paisId: string;
  paisNome: string;
  ouro: number;
  prata: number;
  bronze: number;
}
