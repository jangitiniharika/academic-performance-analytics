import usePaginatedList from '../../hooks/usePaginatedList.js';
import DataTable from './DataTable.jsx';
import Pagination from './Pagination.jsx';

/**
 * A read-only paginated table page (used by the student's own-data screens).
 *
 * `loader(params)` must return the backend `PageResponse`.
 */
export default function RecordsPage({
  title,
  subtitle,
  loader,
  columns,
  header,
  size = 10,
  emptyMessage,
}) {
  const list = usePaginatedList((params) => loader(params), { size });

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">{title}</h1>
          {subtitle && <p className="page__subtitle">{subtitle}</p>}
        </div>
      </div>
      {header}
      <div className="card">
        <DataTable
          columns={columns}
          rows={list.rows}
          loading={list.loading}
          error={list.error}
          onRetry={list.reload}
          emptyMessage={emptyMessage}
        />
        <Pagination
          page={list.page}
          totalPages={list.totalPages}
          totalElements={list.totalElements}
          onPageChange={list.setPage}
        />
      </div>
    </section>
  );
}
