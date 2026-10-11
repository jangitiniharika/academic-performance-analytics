import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { assignmentService, submissionService } from '../../services/academicService.js';
import { studentService } from '../../services/managementService.js';
import { toInt, toNumber } from '../../utils/form.js';

const STATUS_OPTIONS = [
  { value: 'PENDING', label: 'Pending' },
  { value: 'SUBMITTED', label: 'Submitted' },
  { value: 'GRADED', label: 'Graded' },
  { value: 'LATE', label: 'Late' },
];

const STATUS_VARIANT = {
  PENDING: 'neutral',
  SUBMITTED: 'info',
  GRADED: 'success',
  LATE: 'warning',
};

const columns = [
  { key: 'student', header: 'Student', render: (row) => row.student?.fullName ?? '—' },
  { key: 'roll', header: 'Roll number', render: (row) => row.student?.rollNumber ?? '—' },
  { key: 'assignmentTitle', header: 'Assignment' },
  {
    key: 'status',
    header: 'Status',
    render: (row) => (
      <span className={`badge badge--${STATUS_VARIANT[row.status] ?? 'neutral'}`}>{row.status}</span>
    ),
  },
  { key: 'marks', header: 'Marks', render: (row) => (row.marks == null ? '—' : row.marks) },
  { key: 'maxMarks', header: 'Max' },
  { key: 'feedback', header: 'Feedback', render: (row) => row.feedback || '—' },
];

export default function SubmissionsPage() {
  const assignmentOptions = useOptions(assignmentService.list, (item) => ({
    value: String(item.id),
    label: item.title,
  }));
  const studentOptions = useOptions(studentService.list, (item) => ({
    value: String(item.id),
    label: `${item.rollNumber} — ${item.fullName}`,
  }));

  const fields = [
    { name: 'assignmentId', label: 'Assignment', type: 'select', required: true, options: assignmentOptions, parse: toInt },
    { name: 'studentId', label: 'Student', type: 'select', required: true, options: studentOptions, parse: toInt },
    { name: 'status', label: 'Status', type: 'select', options: STATUS_OPTIONS, defaultValue: 'SUBMITTED' },
    { name: 'marks', label: 'Awarded marks', type: 'number', step: '0.5', parse: toNumber },
    { name: 'feedback', label: 'Feedback', type: 'textarea' },
  ];

  const filters = [
    { name: 'assignmentId', label: 'Assignment', type: 'select', options: assignmentOptions },
    { name: 'studentId', label: 'Student', type: 'select', options: studentOptions },
  ];

  return (
    <CrudResourcePage
      title="Submission"
      heading="Submissions"
      subtitle="Track submission status and grade coursework."
      service={submissionService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        assignmentId: row.assignmentId != null ? String(row.assignmentId) : '',
        studentId: row.student ? String(row.student.id) : '',
        status: row.status ?? 'SUBMITTED',
        marks: row.marks ?? '',
        feedback: row.feedback ?? '',
      })}
      emptyMessage="No submissions recorded yet."
    />
  );
}
