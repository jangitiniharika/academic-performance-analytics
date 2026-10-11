import { Link } from 'react-router-dom';

import { useAuth } from '../auth/AuthContext.jsx';

export default function ForbiddenPage() {
  const { user } = useAuth();

  return (
    <div className="page">
      <div className="card state state--error" style={{ padding: '3rem' }}>
        <span className="state__icon" aria-hidden="true">
          403
        </span>
        <div>
          <div className="state__title">Access denied</div>
          <div className="state__message">
            {user
              ? `Your ${user.role} account does not have permission to view this page.`
              : 'You are not signed in.'}
          </div>
        </div>
        <Link className="btn btn--primary" to="/app/dashboard">
          Go to my dashboard
        </Link>
      </div>
    </div>
  );
}
