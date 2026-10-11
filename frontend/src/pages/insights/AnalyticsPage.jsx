import { useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';

import useAsync from '../../hooks/useAsync.js';
import useOptions from '../../hooks/useOptions.js';
import { analyticsService } from '../../services/insightService.js';
import { subjectService } from '../../services/managementService.js';
import StatCard from '../../components/common/StatCard.jsx';
import { EmptyState, ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import { formatNumber } from '../../utils/format.js';

export default function AnalyticsPage() {
  const [subjectId, setSubjectId] = useState('');

  const overview = useAsync(() => analyticsService.overview(), []);
  const classData = useAsync(
    () => (subjectId ? analyticsService.classAnalytics(subjectId) : Promise.resolve(null)),
    [subjectId],
  );
  const subjectOptions = useOptions(subjectService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));

  if (overview.status === 'loading') {
    return <LoadingState message="Crunching analytics…" />;
  }
  if (overview.status === 'error') {
    return <ErrorState message={overview.error.message} onRetry={overview.reload} />;
  }

  const data = overview.data;
  const distribution = classData.data?.distribution ?? [];

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">Analytics</h1>
          <p className="page__subtitle">Institution summary and per-subject performance distribution.</p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={overview.reload}>
          Refresh
        </button>
      </div>

      <div className="grid grid--stats">
        <StatCard
          label="Average attendance"
          value={data.averageAttendancePercentage == null ? 'No data' : `${formatNumber(data.averageAttendancePercentage)}%`}
          icon="◔"
        />
        <StatCard
          label="Average marks"
          value={data.averageMarksPercentage == null ? 'No data' : `${formatNumber(data.averageMarksPercentage)}%`}
          icon="∑"
          accent="emerald"
        />
        <StatCard
          label="Average CGPA"
          value={data.averageCgpa == null ? 'No data' : formatNumber(data.averageCgpa)}
          icon="★"
          accent="violet"
        />
        <StatCard
          label="High risk students"
          value={data.highRiskStudents}
          icon="⚠"
          accent="amber"
          hint={`${data.mediumRiskStudents} medium risk · ${data.openAlerts} alerts`}
        />
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Class performance</h2>
            <p className="card__subtitle">Pick a subject to see its marks and attendance profile.</p>
          </div>
          <label className="toolbar__field">
            <span className="toolbar__label">Subject</span>
            <select
              className="input"
              value={subjectId}
              onChange={(event) => setSubjectId(event.target.value)}
            >
              <option value="">Select a subject…</option>
              {subjectOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
        </div>

        {!subjectId && <EmptyState title="No subject selected" message="Choose a subject above." />}

        {subjectId && classData.status === 'loading' && <LoadingState />}
        {subjectId && classData.status === 'error' && (
          <ErrorState message={classData.error.message} onRetry={classData.reload} />
        )}

        {classData.status === 'success' && classData.data && (
          <>
            <div className="grid grid--stats">
              <StatCard label="Students in department" value={classData.data.studentCount} />
              <StatCard
                label="Average marks"
                value={
                  classData.data.averageMarksPercentage == null
                    ? 'No data'
                    : `${formatNumber(classData.data.averageMarksPercentage)}%`
                }
                accent="emerald"
              />
              <StatCard
                label="Average attendance"
                value={
                  classData.data.averageAttendancePercentage == null
                    ? 'No data'
                    : `${formatNumber(classData.data.averageAttendancePercentage)}%`
                }
                accent="violet"
              />
            </div>

            <div className="chart">
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={distribution} margin={{ top: 8, right: 16, bottom: 8, left: -12 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />
                  <XAxis dataKey="label" tick={{ fontSize: 12 }} />
                  <YAxis allowDecimals={false} width={40} />
                  <Tooltip />
                  <Bar dataKey="count" fill="#4338ca" radius={[6, 6, 6, 6]} />
                </BarChart>
              </ResponsiveContainer>
              <p className="muted">
                Students grouped by their average marks in the selected subject.
              </p>
            </div>
          </>
        )}
      </div>
    </section>
  );
}
