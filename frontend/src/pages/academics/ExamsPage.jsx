import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { examService } from '../../services/academicService.js';
import { subjectService } from '../../services/managementService.js';
import { toInt } from '../../utils/form.js';

const columns = [
  { key: 'name', header: 'Exam' },
  { key: 'subject', header: 'Subject', render: (row) => row.subject?.code ?? '—' },
  { key: 'examDate', header: 'Date' },
  { key: 'maxMarks', header: 'Max marks' },
];

export default function ExamsPage() {
  const subjectOptions = useOptions(subjectService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));

  const fields = [
    { name: 'name', label: 'Exam name', required: true, placeholder: 'e.g. Mid-Term' },
    { name: 'subjectId', label: 'Subject', type: 'select', required: true, options: subjectOptions, parse: toInt },
    { name: 'examDate', label: 'Exam date', type: 'date', required: true },
    { name: 'maxMarks', label: 'Maximum marks', type: 'number', required: true, parse: toInt, defaultValue: 100 },
  ];

  const filters = [{ name: 'subjectId', label: 'Subject', type: 'select', options: subjectOptions }];

  return (
    <CrudResourcePage
      title="Exam"
      heading="Exams"
      subtitle="Assessment definitions per subject."
      service={examService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        name: row.name,
        subjectId: row.subject ? String(row.subject.id) : '',
        examDate: row.examDate ?? '',
        maxMarks: row.maxMarks ?? '',
      })}
      emptyMessage="No exams yet. Create the first one."
    />
  );
}
