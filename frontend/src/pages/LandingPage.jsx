import { Link } from 'react-router-dom';

import useAsync from '../hooks/useAsync.js';
import { fetchHealth } from '../services/healthService.js';
import { API_BASE_URL } from '../api/client.js';
import StatCard from '../components/common/StatCard.jsx';
import StatusBadge from '../components/common/StatusBadge.jsx';
import { ErrorState, LoadingState } from '../components/common/StateViews.jsx';

/** The 21 delivery stages from the project plan, with their current state. */
const STAGES = [
  'Project setup and architecture',
  'MySQL configuration and database entities',
  'Authentication and JWT security',
  'Student, faculty, department and subject management',
  'Attendance management',
  'Exam and marks management',
  'Assignment management',
  'Academic results',
  'Analytics',
  'Performance prediction',
  'Early-warning alerts',
  'REST API verification',
  'React frontend setup',
  'Login and role-based routing',
  'Student dashboard',
  'Faculty dashboard',
  'Admin dashboard',
  'Charts and analytics UI',
  'Search, filters, pagination and reports',
  'Testing, bug fixing and security review',
  'Documentation and final verification',
];

export default function LandingPage() {
  const { status, data: health, error, reload } = useAsync(() => fetchHealth(), []);

  const backendUp = status === 'success' && health?.status === 'UP';

  return (
    <div className="page">
      <section className="hero card">
        <div className="hero__content">
          <span className="badge badge--success">Complete platform · all stages delivered</span>
          <h1 className="hero__title">
            Academic Performance Analytics &amp; Prediction Platform
          </h1>
          <p className="hero__lead">
            A centralized system that collects attendance, internal marks, assignment
            scores and previous-semester results, then predicts student performance and
            raises early-warning alerts for students who need support.
          </p>
          <div className="hero__actions">
            <Link className="btn btn--primary" to="/login">
              Sign in
            </Link>
            <Link className="btn btn--ghost" to="/register">
              Create account
            </Link>
          </div>
          <div className="hero__actions">
            <span className="badge badge--neutral">Admin</span>
            <span className="badge badge--neutral">Faculty</span>
            <span className="badge badge--neutral">Student</span>
          </div>
        </div>
      </section>

      <section className="grid grid--stats">
        <StatCard label="Roles" value="3" hint="Admin · Faculty · Student" accent="indigo" icon="◎" />
        <StatCard label="Core Entities" value="13" hint="Users, marks, alerts…" accent="violet" icon="▦" />
        <StatCard label="Prediction Model" value="Rule-based" hint="Weighted, transparent score" accent="emerald" icon="∑" />
        <StatCard label="Build Progress" value="21 / 21" hint="All stages delivered" accent="amber" icon="◷" />
      </section>

      <section className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Live system status</h2>
            <p className="card__subtitle">
              Fetched from <code>{API_BASE_URL}/health</code> — no hard-coded values.
            </p>
          </div>
          <button type="button" className="btn btn--ghost" onClick={reload}>
            Refresh
          </button>
        </div>

        {status === 'loading' && <LoadingState message="Contacting the backend…" />}

        {status === 'error' && (
          <ErrorState
            title="Backend not reachable"
            message={
              error?.message ||
              'Start the Spring Boot server and the project-local MySQL, then try again.'
            }
            onRetry={reload}
          />
        )}

        {status === 'success' && (
          <div className="health">
            <div className="health__row">
              <span className="health__label">Backend API</span>
              <StatusBadge variant={backendUp ? 'success' : 'danger'}>
                {health.status}
              </StatusBadge>
            </div>
            <div className="health__row">
              <span className="health__label">MySQL database</span>
              <StatusBadge variant={health.database?.status === 'UP' ? 'success' : 'danger'}>
                {health.database?.status} · {health.database?.product}
              </StatusBadge>
            </div>
            <div className="health__row">
              <span className="health__label">Application</span>
              <span className="health__value">{health.application}</span>
            </div>
            <div className="health__row">
              <span className="health__label">Checked at</span>
              <span className="health__value">
                {new Date(health.timestamp).toLocaleString()}
              </span>
            </div>
          </div>
        )}
      </section>

      <section className="card">
        <div className="card__header">
          <h2 className="card__title">Delivery roadmap</h2>
        </div>
        <ol className="roadmap">
          {STAGES.map((title, index) => {
            const done = true;
            return (
              <li key={title} className={`roadmap__item ${done ? 'is-done' : ''}`}>
                <span className="roadmap__index">{index + 1}</span>
                <span className="roadmap__title">{title}</span>
                <StatusBadge variant={done ? 'success' : 'neutral'}>
                  {done ? 'Done' : 'Planned'}
                </StatusBadge>
              </li>
            );
          })}
        </ol>
      </section>
    </div>
  );
}
