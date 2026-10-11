import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { departmentService, studentService } from '../../services/managementService.js';
import { toInt, toNumber } from '../../utils/form.js';

const columns = [
  { key: 'rollNumber', header: 'Roll number' },
  { key: 'fullName', header: 'Name' },
  { key: 'email', header: 'Email' },
  { key: 'department', header: 'Department', render: (row) => row.department?.code ?? '—' },
  { key: 'currentSemester', header: 'Semester' },
  { key: 'cgpa', header: 'CGPA', render: (row) => (row.cgpa ?? '—') },
];

export default function StudentsPage() {
  const departmentOptions = useOptions(departmentService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));

  const fields = [
    { name: 'rollNumber', label: 'Roll number', required: true },
    { name: 'registrationNumber', label: 'Registration number', required: true },
    { name: 'firstName', label: 'First name', required: true },
    { name: 'lastName', label: 'Last name', required: true },
    { name: 'email', label: 'Email', type: 'email', required: true },
    { name: 'phone', label: 'Phone', type: 'text' },
    { name: 'currentSemester', label: 'Current semester', type: 'number', required: true, parse: toInt, defaultValue: 1 },
    { name: 'cgpa', label: 'CGPA', type: 'number', step: '0.01', parse: toNumber },
    { name: 'admissionYear', label: 'Admission year', type: 'number', parse: toInt },
    {
      name: 'departmentId',
      label: 'Department',
      type: 'select',
      required: true,
      options: departmentOptions,
      parse: toInt,
    },
    { name: 'userId', label: 'Linked user id (optional)', type: 'number', parse: toInt },
  ];

  const filters = [
    { name: 'search', label: 'Search', placeholder: 'Roll number or name' },
    { name: 'departmentId', label: 'Department', type: 'select', options: departmentOptions },
  ];

  return (
    <CrudResourcePage
      title="Student"
      heading="Students"
      subtitle="Student records, their department and current standing."
      service={studentService}
      columns={columns}
      fields={fields}
      filters={filters}
      size={10}
      toFormValues={(row) => ({
        rollNumber: row.rollNumber,
        registrationNumber: row.registrationNumber,
        firstName: row.firstName,
        lastName: row.lastName,
        email: row.email,
        phone: row.phone ?? '',
        currentSemester: row.currentSemester,
        cgpa: row.cgpa ?? '',
        admissionYear: row.admissionYear ?? '',
        departmentId: row.department ? String(row.department.id) : '',
        userId: row.userId ?? '',
      })}
      emptyMessage="No students yet. Create the first one."
    />
  );
}
