import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { useAuth } from '../../auth/AuthContext.jsx';
import { departmentService, facultyService, subjectService } from '../../services/managementService.js';
import { toInt } from '../../utils/form.js';

const columns = [
  { key: 'code', header: 'Code' },
  { key: 'name', header: 'Name' },
  { key: 'semester', header: 'Semester' },
  { key: 'credits', header: 'Credits' },
  { key: 'department', header: 'Department', render: (row) => row.department?.code ?? '—' },
  { key: 'facultyId', header: 'Faculty id', render: (row) => row.facultyId ?? '—' },
];

export default function SubjectsPage() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const departmentOptions = useOptions(departmentService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));
  const facultyOptions = useOptions(facultyService.list, (item) => ({
    value: String(item.id),
    label: `${item.employeeCode} — ${item.fullName}`,
  }));

  const fields = [
    { name: 'code', label: 'Code', required: true },
    { name: 'name', label: 'Name', required: true },
    { name: 'semester', label: 'Semester', type: 'number', required: true, parse: toInt, defaultValue: 1 },
    { name: 'credits', label: 'Credits', type: 'number', required: true, parse: toInt, defaultValue: 3 },
    {
      name: 'departmentId',
      label: 'Department',
      type: 'select',
      required: true,
      options: departmentOptions,
      parse: toInt,
    },
    {
      name: 'facultyId',
      label: 'Faculty (optional)',
      type: 'select',
      options: facultyOptions,
      parse: toInt,
    },
  ];

  const filters = [
    { name: 'search', label: 'Search', placeholder: 'Code or name' },
    { name: 'departmentId', label: 'Department', type: 'select', options: departmentOptions },
  ];

  return (
    <CrudResourcePage
      title="Subject"
      heading="Subjects"
      subtitle="Courses taught in each department and semester."
      service={subjectService}
      columns={columns}
      fields={fields}
      filters={filters}
      canCreate={isAdmin}
      canEdit={isAdmin}
      canDelete={isAdmin}
      toFormValues={(row) => ({
        code: row.code,
        name: row.name,
        semester: row.semester,
        credits: row.credits,
        departmentId: row.department ? String(row.department.id) : '',
        facultyId: row.facultyId != null ? String(row.facultyId) : '',
      })}
      emptyMessage="No subjects yet. Create the first one."
    />
  );
}
