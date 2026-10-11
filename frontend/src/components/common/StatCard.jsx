/**
 * Dashboard statistic card: a label, a big value and an optional hint.
 */
export default function StatCard({ label, value, hint, icon, accent = 'indigo' }) {
  return (
    <div className={`stat-card stat-card--${accent}`}>
      <div className="stat-card__head">
        <span className="stat-card__label">{label}</span>
        {icon && <span className="stat-card__icon" aria-hidden="true">{icon}</span>}
      </div>
      <div className="stat-card__value">{value}</div>
      {hint && <div className="stat-card__hint">{hint}</div>}
    </div>
  );
}
