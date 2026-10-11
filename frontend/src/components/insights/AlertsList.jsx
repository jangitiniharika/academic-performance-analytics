import StatusBadge from '../common/StatusBadge.jsx';
import { EmptyState } from '../common/StateViews.jsx';
import { formatDateTime } from '../../utils/format.js';

const SEVERITY_VARIANT = { LOW: 'info', MEDIUM: 'warning', HIGH: 'danger' };

/**
 * A list of early-warning alerts. When `onAcknowledge` is provided each open
 * alert gets an "Acknowledge" action (used by the Admin/Faculty alerts page).
 */
export default function AlertsList({ alerts, onAcknowledge }) {
  if (!alerts || alerts.length === 0) {
    return (
      <EmptyState title="No alerts" message="No early-warning alerts have been raised." />
    );
  }

  return (
    <ul className="alerts">
      {alerts.map((alert) => (
        <li key={alert.id} className="alert">
          <div className="alert__head">
            <StatusBadge variant={SEVERITY_VARIANT[alert.severity] ?? 'neutral'}>
              {alert.severity}
            </StatusBadge>
            <span className="alert__type">{alert.type}</span>
            {alert.acknowledged && <span className="badge badge--neutral">Acknowledged</span>}
          </div>
          <p className="alert__message">{alert.message}</p>
          <div className="alert__foot">
            <span className="muted">
              {alert.student?.rollNumber ? `${alert.student.rollNumber} · ` : ''}
              {formatDateTime(alert.createdAt)}
            </span>
            {onAcknowledge && !alert.acknowledged && (
              <button
                type="button"
                className="btn btn--ghost btn--sm"
                onClick={() => onAcknowledge(alert)}
              >
                Acknowledge
              </button>
            )}
          </div>
        </li>
      ))}
    </ul>
  );
}
