/** Display formatting helpers and shared badge-variant maps. */

/** Formats a numeric value for display, showing a dash when it is missing. */
export const formatNumber = (value, digits = 2) => {
  if (value === null || value === undefined || value === '') {
    return '—';
  }
  return Number(value).toFixed(digits);
};

/** Formats an ISO date-time string for display. */
export const formatDateTime = (value) => {
  if (!value) {
    return '—';
  }
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? '—' : date.toLocaleString();
};

/** Formats a percentage, showing "No data" when it is missing. */
export const formatPercent = (value) => {
  if (value === null || value === undefined || value === '') {
    return 'No data';
  }
  return `${Number(value).toFixed(2)}%`;
};

export const RISK_VARIANT = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' };
export const SEVERITY_VARIANT = { LOW: 'info', MEDIUM: 'warning', HIGH: 'danger' };
export const STATUS_VARIANT = {
  PRESENT: 'success',
  LATE: 'warning',
  ABSENT: 'danger',
  PENDING: 'neutral',
  SUBMITTED: 'info',
  GRADED: 'success',
};
