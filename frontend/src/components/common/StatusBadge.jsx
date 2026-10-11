/**
 * Small coloured status pill used for performance and risk labels.
 * variant: 'success' | 'danger' | 'warning' | 'info' | 'neutral'
 */
export default function StatusBadge({ children, variant = 'neutral', dot = true }) {
  return (
    <span className={`badge badge--${variant}`}>
      {dot && <span className="badge__dot" aria-hidden="true" />}
      {children}
    </span>
  );
}
