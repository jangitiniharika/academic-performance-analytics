import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

import { useAuth } from '../../auth/AuthContext.jsx';

/** Demo accounts from the backend seeder (see README). */
const DEMO_ACCOUNTS = [
  { label: 'Admin', email: 'admin@campus.edu', password: 'Admin@123' },
  { label: 'Faculty', email: 'faculty1@campus.edu', password: 'Faculty@123' },
  { label: 'Student', email: 'student1@campus.edu', password: 'Student@123' },
];

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const redirectTo = location.state?.from || '/app/dashboard';
  const notice = location.state?.notice;

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await login({ email: email.trim(), password });
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(err.message || 'Sign-in failed. Please check your details.');
      setSubmitting(false);
    }
  }

  function fillDemo(account) {
    setEmail(account.email);
    setPassword(account.password);
    setError(null);
  }

  return (
    <div className="auth">
      <div className="auth__card card">
        <h1 className="auth__title">Sign in</h1>
        <p className="auth__subtitle">
          Use your email and password. Accounts are provisioned by the institution.
        </p>

        {notice && <div className="banner banner--success">{notice}</div>}
        {error && <div className="banner banner--error">{error}</div>}

        <form className="form" onSubmit={handleSubmit} noValidate>
          <label className="form__field">
            <span className="form__label">Email address</span>
            <input
              className="input"
              type="email"
              autoComplete="username"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="you@campus.edu"
              required
            />
          </label>
          <label className="form__field">
            <span className="form__label">Password</span>
            <input
              className="input"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="••••••••"
              required
            />
          </label>
          <button type="submit" className="btn btn--primary btn--block" disabled={submitting}>
            {submitting ? 'Signing in…' : 'Sign in'}
          </button>
        </form>

        <div className="auth__demo">
          <span className="auth__demo-label">Demo accounts</span>
          <div className="auth__demo-buttons">
            {DEMO_ACCOUNTS.map((account) => (
              <button
                key={account.label}
                type="button"
                className="btn btn--ghost btn--sm"
                onClick={() => fillDemo(account)}
              >
                {account.label}
              </button>
            ))}
          </div>
        </div>

        <p className="auth__switch">
          New student? <Link to="/register">Create an account</Link>
        </p>
      </div>
    </div>
  );
}
