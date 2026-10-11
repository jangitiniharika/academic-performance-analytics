import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { assignmentService } from '../../services/academicService.js';
import { facultyService, subjectService } from '../../services/managementService.js';
import { toInt } from '../../utils/form.js';

const columns = [
  { key: 'title', header: 'Title' },
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'dueDate', header: 'Due date' },
  { key: 'maxMarks', header: 'Max marks' },
  { key: 'facultyId', header: 'Faculty id', render: (row) => row.facultyId ?? '—' },
];

export default function AssignmentsPage() {
  const subjectOptions = useOptions(subjectService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));
  const facultyOptions = useOptions(facultyService.list, (item) => ({
    value: String(item.id),
    label: `${item.employeeCode} — ${item.fullName}`,
  }));

  const fields = [
    { name: 'title', label: 'Title', required: true },
    { name: 'description', label: 'Description', type: 'textarea' },
    { name: 'subjectId', label: 'Subject', type: 'select', required: true, options: subjectOptions, parse: toInt },
    { name: 'facultyId', label: 'Faculty (optional)', type: 'select', options: facultyOptions, parse: toInt },
    { name: 'dueDate', label: 'Due date', type: 'date', required: true },
    { name: 'maxMarks', label: 'Maximum marks', type: 'number', required: true, parse: toInt, defaultValue: 100 },
  ];

  const filters = [{ name: 'subjectId', label: 'Subject', type: 'select', options: subjectOptions }];

  return (
    <CrudResourcePage
      title="Assignment"
      heading="Assignments"
      subtitle="Coursework set for a subject with a due date and total marks."
      service={assignmentService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        title: row.title,
        description: row.description ?? '',
        subjectId: row.subject ? String(row.subject.id) : '',
        facultyId: row.facultyId != null ? String(row.facultyId) : '',
        dueDate: row.dueDate ?? '',
        maxMarks: row.maxMarks ?? '',
      })}
      emptyMessage="No assignments yet. Create the first one."
    />
  );
}
