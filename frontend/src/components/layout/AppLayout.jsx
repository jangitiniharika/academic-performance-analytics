import { Link, Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../auth/AuthContext.jsx';

const APP_NAME =
  import.meta.env.VITE_APP_NAME ||
  'Academic Performance Analytics & Prediction Platform';

/**
 * Public marketing shell (landing, login, register, 404): top navigation,
 * content and a footer. The authenticated shell lives in `DashboardLayout`.
 */
export default function AppLayout() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/', { replace: true });
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="container app-header__inner">
          <Link className="brand" to="/">
            <span className="brand__mark" aria-hidden="true">
              AP
            </span>
            <span className="brand__text">
              <span className="brand__title">Academic Performance</span>
              <span className="brand__subtitle">Analytics &amp; Prediction Platform</span>
            </span>
          </Link>

          <nav className="topnav" aria-label="Primary">
            <Link className="topnav__link" to="/">
              Overview
            </Link>
            {isAuthenticated ? (
              <>
                <Link className="topnav__link" to="/app/dashboard">
                  Dashboard
                </Link>
                <span className="badge badge--info">{user?.role}</span>
                <button type="button" className="btn btn--ghost btn--sm" onClick={handleLogout}>
                  Sign out
                </button>
              </>
            ) : (
              <>
                <Link className="topnav__link" to="/login">
                  Sign in
                </Link>
                <Link className="btn btn--primary btn--sm" to="/register">
                  Create account
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      <main className="app-main">
        <div className="container">
          <Outlet />
        </div>
      </main>

      <footer className="app-footer">
        <div className="container app-footer__inner">
          <span>{APP_NAME}</span>
          <span className="muted">Java 17 · Spring Boot 3 · React · MySQL</span>
        </div>
      </footer>
    </div>
  );
}
