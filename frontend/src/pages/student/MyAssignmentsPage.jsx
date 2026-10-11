import { myService } from '../../services/insightService.js';
import RecordsPage from '../../components/ui/RecordsPage.jsx';
import StatusBadge from '../../components/common/StatusBadge.jsx';
import { formatDateTime } from '../../utils/format.js';

const STATUS_VARIANT = {
  PENDING: 'neutral',
  SUBMITTED: 'info',
  GRADED: 'success',
  LATE: 'warning',
};

const columns = [
  { key: 'assignmentTitle', header: 'Assignment' },
  {
    key: 'status',
    header: 'Status',
    render: (row) => (
      <StatusBadge variant={STATUS_VARIANT[row.status] ?? 'neutral'}>{row.status}</StatusBadge>
    ),
  },
  { key: 'marks', header: 'Awarded', render: (row) => row.marks ?? '—' },
  { key: 'maxMarks', header: 'Max' },
  { key: 'submittedAt', header: 'Submitted', render: (row) => formatDateTime(row.submittedAt) },
  { key: 'feedback', header: 'Feedback', render: (row) => row.feedback || '—' },
];

export default function MyAssignmentsPage() {
  return (
    <RecordsPage
      title="My assignments"
      subtitle="Submission status and grades for your coursework."
      loader={(params) => myService.assignments(params)}
      columns={columns}
      emptyMessage="No assignments recorded yet."
    />
  );
}
