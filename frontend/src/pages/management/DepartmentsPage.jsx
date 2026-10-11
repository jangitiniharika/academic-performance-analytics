import CrudResourcePage from '../../components/ui/CrudResourcePage.jsx';
import { departmentService } from '../../services/managementService.js';

const columns = [
  { key: 'code', header: 'Code' },
  { key: 'name', header: 'Name' },
  { key: 'description', header: 'Description', render: (row) => row.description || '—' },
];

const fields = [
  { name: 'name', label: 'Name', required: true },
  { name: 'code', label: 'Code', required: true },
  { name: 'description', label: 'Description', type: 'textarea' },
];

const filters = [{ name: 'search', label: 'Search', placeholder: 'Name or code' }];

export default function DepartmentsPage() {
  return (
    <CrudResourcePage
      title="Department"
      heading="Departments"
      subtitle="Academic departments offered by the institution."
      service={departmentService}
      columns={columns}
      fields={fields}
      filters={filters}
      toFormValues={(row) => ({
        name: row.name,
        code: row.code,
        description: row.description ?? '',
      })}
      emptyMessage="No departments yet. Create the first one."
    />
  );
}
