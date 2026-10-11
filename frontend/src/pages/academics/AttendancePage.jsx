import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import StatusBadge from '../../components/common/StatusBadge.jsx';
import useOptions from '../../hooks/useOptions.js';
import { attendanceService } from '../../services/academicService.js';
import { studentService, subjectService } from '../../services/managementService.js';
import { toInt } from '../../utils/form.js';

const STATUS_OPTIONS = [
  { value: 'PRESENT', label: 'Present' },
  { value: 'ABSENT', label: 'Absent' },
  { value: 'LATE', label: 'Late' },
];

const STATUS_VARIANT = { PRESENT: 'success', ABSENT: 'danger', LATE: 'warning' };

export default function AttendancePage() {
  const studentOptions = useOptions(studentService.list, (item) => ({
    value: String(item.id),
    label: `${item.rollNumber} — ${item.fullName}`,
  }));
  const subjectOptions = useOptions(subjectService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));

  const columns = [
    { key: 'student', header: 'Student', render: (row) => row.student?.fullName ?? '—' },
    { key: 'roll', header: 'Roll number', render: (row) => row.student?.rollNumber ?? '—' },
    { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
    { key: 'classDate', header: 'Date' },
    {
      key: 'status',
      header: 'Status',
      render: (row) => (
        <span className={`badge badge--${STATUS_VARIANT[row.status] ?? 'neutral'}`}>
          {row.status}
        </span>
      ),
    },
    { key: 'remarks', header: 'Remarks', render: (row) => row.remarks || '—' },
  ];

  const fields = [
    { name: 'studentId', label: 'Student', type: 'select', required: true, options: studentOptions, parse: toInt },
    { name: 'subjectId', label: 'Subject', type: 'select', required: true, options: subjectOptions, parse: toInt },
    { name: 'classDate', label: 'Class date', type: 'date', required: true },
    { name: 'status', label: 'Status', type: 'select', required: true, options: STATUS_OPTIONS, defaultValue: 'PRESENT' },
    { name: 'remarks', label: 'Remarks', placeholder: 'Optional note' },
  ];

  const filters = [
    { name: 'studentId', label: 'Student', type: 'select', options: studentOptions },
    { name: 'subjectId', label: 'Subject', type: 'select', options: subjectOptions },
  ];

  return (
    <CrudResourcePage
      title="Attendance record"
      heading="Attendance"
      subtitle="One row per student, subject and class date."
      service={attendanceService}
      columns={columns}
      fields={fields}
      filters={filters}
      size={10}
      toFormValues={(row) => ({
        studentId: row.student ? String(row.student.id) : '',
        subjectId: row.subject ? String(row.subject.id) : '',
        classDate: row.classDate ?? '',
        status: row.status ?? 'PRESENT',
        remarks: row.remarks ?? '',
      })}
      emptyMessage="No attendance recorded yet."
    />
  );
}
