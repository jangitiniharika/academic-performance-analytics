import { useCallback, useState } from 'react';

import usePaginatedList from '../../hooks/usePaginatedList';
import DataTable from './DataTable.jsx';
import FormModal from './FormModal.jsx';
import Pagination from './Pagination.jsx';

/**
 * A complete create/read/update/delete screen for a paginated REST resource.
 *
 * It renders a header, optional search/filter toolbar, the table with per-row
 * Edit/Delete buttons, page controls and the create/edit modal. Pages stay tiny
 * by supplying only the column and form-field configuration.
 */
export default function CrudResourcePage({
  title,
  heading,
  subtitle,
  service,
  columns,
  fields,
  filters = [],
  toFormValues = (row) => row,
  emptyMessage,
  canCreate = true,
  canEdit = true,
  canDelete = true,
  headerActions,
  initialFilters = {},
  size = 10,
}) {
  const [filterValues, setFilterValues] = useState(initialFilters);
  const [editing, setEditing] = useState(null);
  const [banner, setBanner] = useState(null);

  const list = usePaginatedList(
    (params) => service.list(params),
    { size, ...initialFilters },
  );

  const openCreate = () => setEditing({ isNew: true, row: null });
  const openEdit = (row) => setEditing({ isNew: false, row });
  const closeForm = () => setEditing(null);

  async function submit(payload) {
    if (editing.isNew) {
      await service.create(payload);
      setBanner({ type: 'success', message: `${title} created.` });
    } else {
      await service.update(editing.row.id, payload);
      setBanner({ type: 'success', message: `${title} updated.` });
    }
    closeForm();
    list.reload();
  }

  async function remove(row) {
    if (!window.confirm('Delete this record? This cannot be undone.')) {
      return;
    }
    try {
      await service.remove(row.id);
      setBanner({ type: 'success', message: `${title} deleted.` });
      list.reload();
    } catch (err) {
      setBanner({ type: 'error', message: err.message });
    }
  }

  function onFilterChange(name, value) {
    setFilterValues((previous) => ({ ...previous, [name]: value }));
    list.setFilter({ [name]: value || undefined });
  }

  const formFields = typeof fields === 'function' ? fields() : fields;
  const initialValues = editing
    ? editing.isNew
      ? {}
      : toFormValues(editing.row)
    : {};

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">{heading ?? title}</h1>
          {subtitle && <p className="page__subtitle">{subtitle}</p>}
        </div>
        <div className="page__actions">
          {headerActions}
          {canCreate && (
            <button type="button" className="btn btn--primary" onClick={openCreate}>
              + New
            </button>
          )}
        </div>
      </div>

      {banner && <div className={`banner banner--${banner.type}`}>{banner.message}</div>}

      {filters.length > 0 && (
        <div className="toolbar">
          {filters.map((filter) =>
            filter.type === 'select' ? (
              <label key={filter.name} className="toolbar__field">
                <span className="toolbar__label">{filter.label}</span>
                <select
                  className="input"
                  value={filterValues[filter.name] ?? ''}
                  onChange={(event) => onFilterChange(filter.name, event.target.value)}
                >
                  <option value="">All</option>
                  {(filter.options ?? []).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>
            ) : (
              <label key={filter.name} className="toolbar__field">
                <span className="toolbar__label">{filter.label}</span>
                <input
                  className="input"
                  type="search"
                  placeholder={filter.placeholder ?? 'Search…'}
                  value={filterValues[filter.name] ?? ''}
                  onChange={(event) => onFilterChange(filter.name, event.target.value)}
                />
              </label>
            ),
          )}
        </div>
      )}

      <div className="card">
        <DataTable
          columns={columns}
          rows={list.rows}
          loading={list.loading}
          error={list.error}
          onRetry={list.reload}
          emptyMessage={emptyMessage}
          renderActions={
            canEdit || canDelete
              ? (row) => (
                  <div className="row-actions">
                    {canEdit && (
                      <button
                        type="button"
                        className="btn btn--ghost btn--sm"
                        onClick={() => openEdit(row)}
                      >
                        Edit
                      </button>
                    )}
                    {canDelete && (
                      <button
                        type="button"
                        className="btn btn--danger-ghost btn--sm"
                        onClick={() => remove(row)}
                      >
                        Delete
                      </button>
                    )}
                  </div>
                )
              : undefined
          }
        />
        <Pagination
          page={list.page}
          totalPages={list.totalPages}
          totalElements={list.totalElements}
          onPageChange={list.setPage}
        />
      </div>

      {editing && (
        <FormModal
          key={editing.isNew ? 'new' : editing.row.id}
          title={editing.isNew ? `New ${title}` : `Edit ${title}`}
          fields={formFields}
          initialValues={initialValues}
          onSubmit={submit}
          onClose={closeForm}
        />
      )}
    </section>
  );
}
