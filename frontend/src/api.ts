import axios from "axios";

/**
 * Padrão `/api`: mesmo host do SPA — o Vite (dev) e o Nginx (Docker) fazem proxy para a API.
 * Defina `VITE_API_URL` (ex.: `http://localhost:8080`) só se quiser chamar a API direto, sem proxy.
 */
const baseURL = import.meta.env.VITE_API_URL ?? "/api";

export const api = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("sgo_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      sessionStorage.removeItem("sgo_token");
      sessionStorage.removeItem("sgo_perfil");
      if (!window.location.pathname.endsWith("/login")) {
        window.location.assign("/login");
      }
    }
    return Promise.reject(err);
  }
);
