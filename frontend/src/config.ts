/**
 * URL base da API. Em dev, Vite costuma usar proxy em `/api`; defina VITE_API_URL para chamar a API direto.
 */
export function resolveApiBaseUrl(viteUrl: string | undefined): string {
  return viteUrl?.trim() ? viteUrl : "/api";
}
