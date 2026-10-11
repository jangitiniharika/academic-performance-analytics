import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import useOptions from '../../hooks/useOptions.js';
import { departmentService, facultyService } from '../../services/managementService.js';
import { toInt } from '../../utils/form.js';

const columns = [
  { key: 'employeeCode', header: 'Employee code' },
  { key: 'fullName', header: 'Name' },
  { key: 'email', header: 'Email' },
  { key: 'designation', header: 'Designation', render: (row) => row.designation || '—' },
  { key: 'department', header: 'Department', render: (row) => row.department?.code ?? '—' },
];

export default function FacultyPage() {
  const departmentOptions = useOptions(departmentService.list, (item) => ({
    value: String(item.id),
    label: `${item.code} — ${item.name}`,
  }));

  const fields = [
    { name: 'employeeCode', label: 'Employee code', required: true },
    { name: 'firstName', label: 'First name', required: true },
    { name: 'lastName', label: 'Last name', required: true },
    { name: 'email', label: 'Email', type: 'email', required: true },
    { name: 'phone', label: 'Phone' },
    { name: 'designation', label: 'Designation' },
    {
      name: 'departmentId',
      label: 'Department',
      type: 'select',
      required: true,
      options: departmentOptions,
      parse: toInt,
    },
    { name: 'userId', label: 'Login user id (optional)', type: 'number', parse: toInt },
  ];

  const filters = [
    { name: 'search', label: 'Search', placeholder: 'Name, code or email' },
    { name: 'departmentId', label: 'Department', type: 'select', options: departmentOptions },
  ];

  return (
    <CrudResourcePage
      title="Faculty member"
      heading="Faculty"
      subtitle="Teaching staff and their department."
      service={facultyService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        employeeCode: row.employeeCode,
        firstName: row.firstName,
        lastName: row.lastName,
        email: row.email,
        phone: row.phone ?? '',
        designation: row.designation ?? '',
        departmentId: row.department ? String(row.department.id) : '',
        userId: row.userId ?? '',
      })}
      emptyMessage="No faculty records yet."
    />
  );
}
