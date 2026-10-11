import useAsync from '../../hooks/useAsync.js';
import { dashboardService } from '../../services/insightService.js';
import StatCard from '../../components/common/StatCard.jsx';
import { ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import AlertsList from '../../components/insights/AlertsList.jsx';
import RiskTable from '../../components/insights/RiskTable.jsx';

export default function FacultyDashboardPage() {
  const { status, data, error, reload } = useAsync(() => dashboardService.faculty(), []);

  if (status === 'loading') {
    return <LoadingState message="Loading faculty dashboard…" />;
  }
  if (status === 'error') {
    return <ErrorState message={error.message} onRetry={reload} />;
  }

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">Faculty dashboard</h1>
          <p className="page__subtitle">Your classes, coursework and students to follow up.</p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={reload}>
          Refresh
        </button>
      </div>

      <div className="grid grid--stats">
        <StatCard label="Students" value={data.totalStudents} icon="◍" />
        <StatCard label="Subjects" value={data.totalSubjects} icon="▦" accent="violet" />
        <StatCard label="Assignments" value={data.totalAssignments} icon="✎" accent="emerald" />
        <StatCard
          label="Pending submissions"
          value={data.pendingSubmissions}
          icon="◷"
          accent="amber"
          hint={`${data.openAlerts} open alerts`}
        />
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Students needing support</h2>
            <p className="card__subtitle">Ranked by the transparent prediction estimate.</p>
          </div>
        </div>
        <RiskTable students={data.atRiskStudents} />
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Recent alerts</h2>
            <p className="card__subtitle">Open early-warning alerts for your students.</p>
          </div>
        </div>
        <AlertsList alerts={data.recentAlerts} />
      </div>
    </section>
  );
}
