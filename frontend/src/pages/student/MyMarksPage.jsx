import { myService } from '../../services/insightService.js';
import RecordsPage from '../../components/ui/RecordsPage.jsx';
import { formatDateTime } from '../../utils/format.js';

const columns = [
  { key: 'examName', header: 'Exam' },
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'maxMarks', header: 'Max' },
  { key: 'marksObtained', header: 'Obtained' },
  { key: 'percentage', header: '%', render: (row) => (row.percentage == null ? '—' : `${row.percentage}%`) },
  { key: 'createdAt', header: 'Recorded', render: (row) => formatDateTime(row.createdAt) },
];

export default function MyMarksPage() {
  return (
    <RecordsPage
      title="My marks"
      subtitle="Internal assessment marks recorded for you."
      loader={(params) => myService.marks(params)}
      columns={columns}
      emptyMessage="No marks recorded yet."
    />
  );
}
