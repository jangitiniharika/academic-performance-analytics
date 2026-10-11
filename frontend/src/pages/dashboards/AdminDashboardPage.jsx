import useAsync from '../../hooks/useAsync.js';
import { dashboardService } from '../../services/insightService.js';
import StatCard from '../../components/common/StatCard.jsx';
import { ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import AlertsList from '../../components/insights/AlertsList.jsx';
import RiskTable from '../../components/insights/RiskTable.jsx';
import { formatNumber } from '../../utils/format.js';

export default function AdminDashboardPage() {
  const { status, data, error, reload } = useAsync(() => dashboardService.admin(), []);

  if (status === 'loading') {
    return <LoadingState message="Loading institution overview…" />;
  }
  if (status === 'error') {
    return <ErrorState message={error.message} onRetry={reload} />;
  }

  const overview = data.overview;

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">Admin dashboard</h1>
          <p className="page__subtitle">Institution-wide performance, risk and alerts.</p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={reload}>
          Refresh
        </button>
      </div>

      <div className="grid grid--stats">
        <StatCard label="Students" value={overview.totalStudents} icon="◍" />
        <StatCard label="Faculty" value={overview.totalFaculty} icon="▤" accent="violet" />
        <StatCard label="Subjects" value={overview.totalSubjects} icon="▦" accent="emerald" />
        <StatCard label="Departments" value={overview.totalDepartments} icon="▣" accent="amber" />
      </div>

      <div className="grid grid--stats">
        <StatCard
          label="Average attendance"
          value={overview.averageAttendancePercentage == null ? 'No data' : `${formatNumber(overview.averageAttendancePercentage)}%`}
          icon="◔"
        />
        <StatCard
          label="Average marks"
          value={overview.averageMarksPercentage == null ? 'No data' : `${formatNumber(overview.averageMarksPercentage)}%`}
          icon="∑"
          accent="emerald"
        />
        <StatCard
          label="Average CGPA"
          value={overview.averageCgpa == null ? 'No data' : formatNumber(overview.averageCgpa)}
          icon="★"
          accent="violet"
        />
        <StatCard
          label="Open alerts"
          value={overview.openAlerts}
          icon="⚠"
          accent="amber"
          hint={`${overview.highRiskStudents} high · ${overview.mediumRiskStudents} medium risk`}
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
            <p className="card__subtitle">Open early-warning alerts across the institution.</p>
          </div>
        </div>
        <AlertsList alerts={data.recentAlerts} />
      </div>
    </section>
  );
}
