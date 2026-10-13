import { Link } from 'react-router-dom';

const APP_NAME =
  import.meta.env.VITE_APP_NAME ||
  'Academic Performance Analytics & Prediction Platform';

/**
 * Public homepage.
 *
 * Deliberately minimal: only the platform name, a welcome heading, one short
 * message and the two entry points (Login / Sign Up). All technical/status
 * content was removed. Login, registration, routing, auth and the dashboards
 * are unaffected.
 */
export default function LandingPage() {
  return (
    <section className="landing">
      <div className="landing__panel">
        <span className="landing__mark" aria-hidden="true">
          AP
        </span>

        <p className="landing__name">{APP_NAME}</p>

        <h1 className="landing__title">Welcome to Academic Performance Analytics</h1>

        <p className="landing__message">
          Empowering students and faculty with smarter insights for better academic performance.
        </p>

        <div className="landing__actions">
          <Link className="btn btn--primary landing__btn" to="/login">
            Login
          </Link>
          <Link className="btn btn--ghost landing__btn" to="/register">
            Sign Up
          </Link>
        </div>
      </div>
    </section>
  );
}
