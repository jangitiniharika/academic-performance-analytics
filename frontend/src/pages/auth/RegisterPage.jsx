import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { authService } from '../../services/authService.js';

export default function RegisterPage() {
  const navigate = useNavigate();

  const [values, setValues] = useState({ username: '', email: '', password: '' });
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  function setValue(name, value) {
    setValues((previous) => ({ ...previous, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setFieldErrors({});
    try {
      await authService.register({
        username: values.username.trim(),
        email: values.email.trim(),
        password: values.password,
      });
      navigate('/login', {
        replace: true,
        state: { notice: 'Account created. Please sign in with your email and password.' },
      });
    } catch (err) {
      setError(err.message || 'Registration failed.');
      if (err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
      }
      setSubmitting(false);
    }
  }

  return (
    <div className="auth">
      <div className="auth__card card">
        <h1 className="auth__title">Create a student account</h1>
        <p className="auth__subtitle">
          Self-registration always creates a STUDENT account. Ask an administrator for
          Faculty or Admin access.
        </p>

        {error && <div className="banner banner--error">{error}</div>}

        <form className="form" onSubmit={handleSubmit} noValidate>
          <label className="form__field">
            <span className="form__label">Username</span>
            <input
              className="input"
              type="text"
              value={values.username}
              onChange={(event) => setValue('username', event.target.value)}
              placeholder="At least 3 characters"
              required
            />
            {fieldErrors.username && <span className="form__error">{fieldErrors.username}</span>}
          </label>
          <label className="form__field">
            <span className="form__label">Email address</span>
            <input
              className="input"
              type="email"
              value={values.email}
              onChange={(event) => setValue('email', event.target.value)}
              placeholder="you@campus.edu"
              required
            />
            {fieldErrors.email && <span className="form__error">{fieldErrors.email}</span>}
          </label>
          <label className="form__field">
            <span className="form__label">Password</span>
            <input
              className="input"
              type="password"
              value={values.password}
              onChange={(event) => setValue('password', event.target.value)}
              placeholder="At least 8 characters"
              required
            />
            {fieldErrors.password && <span className="form__error">{fieldErrors.password}</span>}
          </label>
          <button type="submit" className="btn btn--primary btn--block" disabled={submitting}>
            {submitting ? 'Creating account…' : 'Create account'}
          </button>
        </form>

        <p className="auth__switch">
          Already registered? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
