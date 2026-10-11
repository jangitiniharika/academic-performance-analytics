import { NavLink, Outlet, useNavigate } from 'react-router-dom';

import { useAuth } from '../../auth/AuthContext.jsx';

/** Sidebar navigation for each role. */
const NAV = {
  ADMIN: [
    { to: '/app/dashboard', label: 'Dashboard', end: true },
    { to: '/app/departments', label: 'Departments' },
    { to: '/app/subjects', label: 'Subjects' },
    { to: '/app/students', label: 'Students' },
    { to: '/app/faculty', label: 'Faculty' },
    { to: '/app/attendance', label: 'Attendance' },
    { to: '/app/exams', label: 'Exams' },
    { to: '/app/marks', label: 'Marks' },
    { to: '/app/assignments', label: 'Assignments' },
    { to: '/app/submissions', label: 'Submissions' },
    { to: '/app/results', label: 'Results' },
    { to: '/app/analytics', label: 'Analytics' },
    { to: '/app/predictions', label: 'Predictions' },
    { to: '/app/alerts', label: 'Alerts' },
  ],
  FACULTY: [
    { to: '/app/dashboard', label: 'Dashboard', end: true },
    { to: '/app/attendance', label: 'Attendance' },
    { to: '/app/exams', label: 'Exams' },
    { to: '/app/marks', label: 'Marks' },
    { to: '/app/assignments', label: 'Assignments' },
    { to: '/app/submissions', label: 'Submissions' },
    { to: '/app/results', label: 'Results' },
    { to: '/app/analytics', label: 'Analytics' },
    { to: '/app/predictions', label: 'Predictions' },
    { to: '/app/alerts', label: 'Alerts' },
  ],
  STUDENT: [
    { to: '/app/dashboard', label: 'Dashboard', end: true },
    { to: '/app/my/attendance', label: 'My Attendance' },
    { to: '/app/my/marks', label: 'My Marks' },
    { to: '/app/my/assignments', label: 'My Assignments' },
    { to: '/app/my/results', label: 'My Results' },
    { to: '/app/my/prediction', label: 'My Prediction' },
  ],
};

/**
 * The authenticated application shell: a role-aware sidebar, a top bar with the
 * signed-in user and a sign-out button, and the routed page content.
 */
export default function DashboardLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const items = NAV[user?.role] ?? [];

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <div className="dash">
      <aside className="dash__sidebar">
        <div className="dash__brand">
          <span className="brand__mark" aria-hidden="true">
            AP
          </span>
          <div className="dash__brand-text">
            <span className="dash__brand-title">Academic Performance</span>
            <span className="dash__brand-sub">Analytics &amp; Prediction</span>
          </div>
        </div>
        <nav className="dash__nav" aria-label="Sections">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) => `dash__link${isActive ? ' is-active' : ''}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="dash__main">
        <header className="dash__topbar">
          <span className="dash__crumb">Academic Performance Platform</span>
          <div className="dash__user">
            <span className="badge badge--info">{user?.role}</span>
            <span className="dash__username">{user?.username}</span>
            <button type="button" className="btn btn--ghost btn--sm" onClick={handleLogout}>
              Sign out
            </button>
          </div>
        </header>
        <main className="dash__content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
