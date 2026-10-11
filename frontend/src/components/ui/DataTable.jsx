import { EmptyState, ErrorState, LoadingState } from '../common/StateViews.jsx';

/**
 * Renders a list of records as a table.
 *
 * columns: [{ key, header, render?(row) }]
 * renderActions?(row) adds a trailing actions column.
 */
export default function DataTable({
  columns,
  rows,
  loading,
  error,
  onRetry,
  emptyMessage = 'No records found.',
  renderActions,
  keyField = 'id',
}) {
  if (loading) {
    return <LoadingState message="Loading records…" />;
  }
  if (error) {
    return <ErrorState message={error.message} onRetry={onRetry} />;
  }
  if (!rows || rows.length === 0) {
    return <EmptyState message={emptyMessage} />;
  }

  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key} scope="col">
                {column.header}
              </th>
            ))}
            {renderActions && (
              <th scope="col" className="data-table__actions-col">
                Actions
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={row[keyField] ?? index}>
              {columns.map((column) => (
                <td key={column.key}>
                  {column.render ? column.render(row) : row[column.key]}
                </td>
              ))}
              {renderActions && (
                <td className="data-table__actions">{renderActions(row)}</td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
