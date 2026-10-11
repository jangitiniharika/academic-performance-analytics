import useAsync from '../../hooks/useAsync.js';
import { myService } from '../../services/insightService.js';
import StatCard from '../../components/common/StatCard.jsx';
import { ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import RecordsPage from '../../components/ui/RecordsPage.jsx';
import { formatNumber } from '../../utils/format.js';

const columns = [
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'semester', header: 'Semester' },
  { key: 'grade', header: 'Grade' },
  { key: 'gradePoints', header: 'Grade points' },
  { key: 'credits', header: 'Credits' },
];

export default function MyResultsPage() {
  const cgpa = useAsync(() => myService.cgpa(), []);

  let header;
  if (cgpa.status === 'loading') {
    header = <LoadingState message="Loading CGPA…" />;
  } else if (cgpa.status === 'error') {
    header = <ErrorState message={cgpa.error.message} onRetry={cgpa.reload} />;
  } else {
    const data = cgpa.data;
    header = (
      <div className="grid grid--stats">
        <StatCard
          label="CGPA"
          value={data.cgpa == null ? 'No data' : formatNumber(data.cgpa)}
          hint={`${data.totalCredits} credits · ${data.subjectsCount} subjects`}
          icon="★"
          accent="violet"
        />
      </div>
    );
  }

  return (
    <RecordsPage
      title="My results"
      subtitle="Semester grades and credits."
      loader={(params) => myService.results(params)}
      columns={columns}
      header={header}
      emptyMessage="No results recorded yet."
    />
  );
}
