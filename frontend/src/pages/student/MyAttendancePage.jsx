import useAsync from '../../hooks/useAsync.js';
import { myService } from '../../services/insightService.js';
import StatCard from '../../components/common/StatCard.jsx';
import { ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import RecordsPage from '../../components/ui/RecordsPage.jsx';
import { formatNumber } from '../../utils/format.js';

const columns = [
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'classDate', header: 'Date' },
  { key: 'status', header: 'Status' },
  { key: 'remarks', header: 'Remarks', render: (row) => row.remarks || '—' },
];

export default function MyAttendancePage() {
  const percentage = useAsync(() => myService.attendancePercentage(), []);

  let header;
  if (percentage.status === 'loading') {
    header = <LoadingState message="Loading attendance summary…" />;
  } else if (percentage.status === 'error') {
    header = <ErrorState message={percentage.error.message} onRetry={percentage.reload} />;
  } else {
    const data = percentage.data;
    header = (
      <div className="grid grid--stats">
        <StatCard
          label="Overall attendance"
          value={data.percentage == null ? 'No data' : `${formatNumber(data.percentage)}%`}
          hint={`${data.attended} attended · ${data.total} total`}
          icon="◔"
        />
      </div>
    );
  }

  return (
    <RecordsPage
      title="My attendance"
      subtitle="Every class session recorded for you."
      loader={(params) => myService.attendance(params)}
      columns={columns}
      header={header}
      emptyMessage="No attendance recorded yet."
    />
  );
}
