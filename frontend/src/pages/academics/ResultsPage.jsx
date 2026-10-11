import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { resultService } from '../../services/academicService.js';
import { studentService, subjectService } from '../../services/managementService.js';
import { toInt, toNumber } from '../../utils/form.js';

const columns = [
  { key: 'student', header: 'Student', render: (row) => row.student?.fullName ?? '—' },
  { key: 'roll', header: 'Roll number', render: (row) => row.student?.rollNumber ?? '—' },
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'semester', header: 'Semester' },
  { key: 'grade', header: 'Grade' },
  { key: 'gradePoints', header: 'Grade points' },
  { key: 'credits', header: 'Credits' },
];

export default function ResultsPage() {
  const studentOptions = useOptions(studentService.list, (item) => ({
    value: String(item.id),
    label: `${item.rollNumber} — ${item.fullName}`,
  }));
  const subjectOptions = useOptions(subjectService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));

  const fields = [
    { name: 'studentId', label: 'Student', type: 'select', required: true, options: studentOptions, parse: toInt },
    { name: 'subjectId', label: 'Subject', type: 'select', required: true, options: subjectOptions, parse: toInt },
    { name: 'semester', label: 'Semester', type: 'number', required: true, parse: toInt, defaultValue: 1 },
    { name: 'grade', label: 'Grade', required: true, placeholder: 'e.g. A' },
    { name: 'gradePoints', label: 'Grade points', type: 'number', step: '0.1', required: true, parse: toNumber },
    { name: 'credits', label: 'Credits', type: 'number', required: true, parse: toInt, defaultValue: 3 },
  ];

  const filters = [
    { name: 'studentId', label: 'Student', type: 'select', options: studentOptions },
    { name: 'semester', label: 'Semester', placeholder: 'e.g. 3' },
  ];

  return (
    <CrudResourcePage
      title="Result"
      heading="Academic results"
      subtitle="Semester grades and credits. CGPA is recalculated automatically."
      service={resultService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        studentId: row.student ? String(row.student.id) : '',
        subjectId: row.subject ? String(row.subject.id) : '',
        semester: row.semester ?? '',
        grade: row.grade ?? '',
        gradePoints: row.gradePoints ?? '',
        credits: row.credits ?? '',
      })}
      emptyMessage="No results recorded yet."
    />
  );
}
