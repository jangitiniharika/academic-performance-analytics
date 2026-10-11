import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';

import useAsync from '../../hooks/useAsync.js';
import { dashboardService } from '../../services/insightService.js';
import StatCard from '../../components/common/StatCard.jsx';
import { EmptyState, ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import AlertsList from '../../components/insights/AlertsList.jsx';
import PredictionPanel from '../../components/insights/PredictionPanel.jsx';
import { formatNumber } from '../../utils/format.js';

export default function StudentDashboardPage() {
  const { status, data, error, reload } = useAsync(() => dashboardService.student(), []);

  if (status === 'loading') {
    return <LoadingState message="Loading your dashboard…" />;
  }
  if (status === 'error') {
    return <ErrorState message={error.message} onRetry={reload} />;
  }

  const analytics = data.analytics;
  const trend = (analytics.marksTrend ?? []).map((point) => ({
    label: point.label,
    percentage: point.percentage == null ? null : Number(point.percentage),
  }));

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">
            Welcome, {analytics.fullName}
            {analytics.rollNumber ? ` (${analytics.rollNumber})` : ''}
          </h1>
          <p className="page__subtitle">
            {analytics.departmentCode ? `${analytics.departmentCode} · ` : ''}
            Your attendance, marks, assignments and predicted performance.
          </p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={reload}>
          Refresh
        </button>
      </div>

      <div className="grid grid--stats">
        <StatCard
          label="Attendance"
          value={analytics.attendancePercentage == null ? 'No data' : `${formatNumber(analytics.attendancePercentage)}%`}
          icon="◔"
        />
        <StatCard
          label="Average marks"
          value={analytics.averageMarksPercentage == null ? 'No data' : `${formatNumber(analytics.averageMarksPercentage)}%`}
          icon="∑"
          accent="emerald"
        />
        <StatCard
          label="Assignments"
          value={analytics.assignmentPercentage == null ? 'No data' : `${formatNumber(analytics.assignmentPercentage)}%`}
          icon="✎"
          accent="amber"
        />
        <StatCard
          label="CGPA"
          value={analytics.cgpa == null ? 'No data' : formatNumber(analytics.cgpa)}
          icon="★"
          accent="violet"
        />
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Predicted performance</h2>
            <p className="card__subtitle">A transparent weighted estimate, not a final grade.</p>
          </div>
        </div>
        <PredictionPanel prediction={analytics.prediction} />
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Marks trend</h2>
            <p className="card__subtitle">Percentage scored, oldest first.</p>
          </div>
        </div>
        {trend.length === 0 ? (
          <EmptyState title="No marks yet" message="Your exam results will appear here." />
        ) : (
          <div className="chart">
            <ResponsiveContainer width="100%" height={280}>
              <LineChart data={trend} margin={{ top: 8, right: 16, bottom: 8, left: -12 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="label" tick={{ fontSize: 12 }} />
                <YAxis domain={[0, 100]} width={40} />
                <Tooltip formatter={(value) => `${value}%`} />
                <Line
                  type="monotone"
                  dataKey="percentage"
                  stroke="#4338ca"
                  strokeWidth={2}
                  dot={{ r: 3 }}
                  connectNulls
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Subject performance</h2>
            <p className="card__subtitle">Attendance and marks per subject.</p>
          </div>
        </div>
        {(!analytics.subjects || analytics.subjects.length === 0) ? (
          <EmptyState title="No subject data yet" />
        ) : (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th scope="col">Subject</th>
                  <th scope="col">Name</th>
                  <th scope="col">Attendance</th>
                  <th scope="col">Marks</th>
                  <th scope="col">Exams</th>
                </tr>
              </thead>
              <tbody>
                {analytics.subjects.map((subject) => (
                  <tr key={subject.subjectId}>
                    <td>{subject.subjectCode}</td>
                    <td>{subject.subjectName}</td>
                    <td>
                      {subject.attendancePercentage == null
                        ? 'No data'
                        : `${formatNumber(subject.attendancePercentage)}%`}
                    </td>
                    <td>
                      {subject.marksPercentage == null
                        ? 'No data'
                        : `${formatNumber(subject.marksPercentage)}%`}
                    </td>
                    <td>{subject.examCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">My alerts</h2>
            <p className="card__subtitle">Early-warning alerts raised for you.</p>
          </div>
        </div>
        <AlertsList alerts={data.alerts} />
      </div>
    </section>
  );
}
