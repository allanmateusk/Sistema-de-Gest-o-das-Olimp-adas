import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/useAuth";

export function RequireAdmin() {
  const { token, isAdmin } = useAuth();
  if (!token) return <Navigate to="/login" replace />;
  if (!isAdmin) return <Navigate to="/" replace />;
  return <Outlet />;
}
