/** Shared loading, error and empty states used by every data-driven screen. */

export function LoadingState({ message = 'Loading…' }) {
  return (
    <div className="state state--loading" role="status" aria-live="polite">
      <span className="spinner" aria-hidden="true" />
      <span>{message}</span>
    </div>
  );
}

export function ErrorState({ title = 'Something went wrong', message, onRetry }) {
  return (
    <div className="state state--error" role="alert">
      <span className="state__icon" aria-hidden="true">
        !
      </span>
      <div>
        <div className="state__title">{title}</div>
        {message && <div className="state__message">{message}</div>}
      </div>
      {onRetry && (
        <button type="button" className="btn btn--ghost" onClick={onRetry}>
          Try again
        </button>
      )}
    </div>
  );
}

export function EmptyState({ title = 'Nothing here yet', message }) {
  return (
    <div className="state state--empty">
      <span className="state__icon" aria-hidden="true">
        ∅
      </span>
      <div>
        <div className="state__title">{title}</div>
        {message && <div className="state__message">{message}</div>}
      </div>
    </div>
  );
}
