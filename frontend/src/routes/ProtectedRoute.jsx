import { Navigate, Outlet, useLocation } from 'react-router-dom';

import { useAuth } from '../auth/AuthContext';
import { LoadingState } from '../components/common/StateViews.jsx';

/**
 * Route guard.
 *
 * - while the session is being restored it shows a spinner,
 * - anonymous users are sent to the login page (remembering where they wanted
 *   to go),
 * - a logged-in user without one of the allowed roles is sent to /forbidden.
 */
export default function ProtectedRoute({ roles }) {
  const { isAuthenticated, user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <LoadingState message="Checking your session…" />;
  }
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/forbidden" replace />;
  }
  return <Outlet />;
}
