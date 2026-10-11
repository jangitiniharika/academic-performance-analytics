/**
 * Page controls for a paginated list.
 *
 * `page` is zero-based (matching the backend `PageResponse`).
 */
export default function Pagination({ page, totalPages, totalElements, onPageChange }) {
  const lastPage = Math.max(0, (totalPages ?? 1) - 1);

  return (
    <div className="pagination">
      <span className="muted">
        {totalElements} record{totalElements === 1 ? '' : 's'} · page {page + 1} of {lastPage + 1}
      </span>
      <div className="pagination__controls">
        <button
          type="button"
          className="btn btn--ghost btn--sm"
          onClick={() => onPageChange(0)}
          disabled={page <= 0}
        >
          « First
        </button>
        <button
          type="button"
          className="btn btn--ghost btn--sm"
          onClick={() => onPageChange(page - 1)}
          disabled={page <= 0}
        >
          Previous
        </button>
        <button
          type="button"
          className="btn btn--ghost btn--sm"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= lastPage}
        >
          Next
        </button>
        <button
          type="button"
          className="btn btn--ghost btn--sm"
          onClick={() => onPageChange(lastPage)}
          disabled={page >= lastPage}
        >
          Last »
        </button>
      </div>
    </div>
  );
}
