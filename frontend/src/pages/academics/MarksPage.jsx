import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { examService, marksService } from '../../services/academicService.js';
import { studentService } from '../../services/managementService.js';
import { toInt, toNumber } from '../../utils/form.js';

const columns = [
  { key: 'student', header: 'Student', render: (row) => row.student?.fullName ?? '—' },
  { key: 'roll', header: 'Roll number', render: (row) => row.student?.rollNumber ?? '—' },
  { key: 'examName', header: 'Exam' },
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'maxMarks', header: 'Max' },
  { key: 'marksObtained', header: 'Obtained' },
  { key: 'percentage', header: '%', render: (row) => (row.percentage == null ? '—' : `${row.percentage}%`) },
];

export default function MarksPage() {
  const studentOptions = useOptions(studentService.list, (item) => ({
    value: String(item.id),
    label: `${item.rollNumber} — ${item.fullName}`,
  }));
  const examOptions = useOptions(examService.list, (item) => ({
    value: String(item.id),
    label: `${item.name} (${item.subject?.code ?? '—'})`,
  }));

  const fields = [
    { name: 'studentId', label: 'Student', type: 'select', required: true, options: studentOptions, parse: toInt },
    { name: 'examId', label: 'Exam', type: 'select', required: true, options: examOptions, parse: toInt },
    {
      name: 'marksObtained',
      label: 'Marks obtained',
      type: 'number',
      step: '0.5',
      required: true,
      parse: toNumber,
    },
  ];

  const filters = [
    { name: 'studentId', label: 'Student', type: 'select', options: studentOptions },
    { name: 'examId', label: 'Exam', type: 'select', options: examOptions },
  ];

  return (
    <CrudResourcePage
      title="Marks entry"
      heading="Marks"
      subtitle="Internal marks recorded against each exam."
      service={marksService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        studentId: row.student ? String(row.student.id) : '',
        examId: row.examId != null ? String(row.examId) : '',
        marksObtained: row.marksObtained ?? '',
      })}
      emptyMessage="No marks recorded yet."
    />
  );
}
