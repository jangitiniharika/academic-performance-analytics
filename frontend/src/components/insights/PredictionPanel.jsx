import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';

import StatusBadge from '../common/StatusBadge.jsx';
import { EmptyState } from '../common/StateViews.jsx';
import { formatDateTime } from '../../utils/format.js';

const RISK_VARIANT = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' };

const COMPONENT_LABELS = [
  ['Previous results', 'previousScore', '40%'],
  ['Internal marks', 'internalsScore', '30%'],
  ['Attendance', 'attendanceScore', '15%'],
  ['Assignments', 'assignmentsScore', '15%'],
];

/**
 * Shows a rule-based prediction: the estimate, its risk band, the four weighted
 * component scores and - importantly - which indicators had no data. Predicted
 * scores are always labelled as an estimate, never as a guaranteed outcome.
 */
export default function PredictionPanel({ prediction }) {
  if (!prediction) {
    return <EmptyState title="No prediction yet" message="Run a prediction to see the estimate." />;
  }

  const components = COMPONENT_LABELS.map(([label, key, weight]) => ({
    label,
    weight,
    value: prediction[key] == null ? null : Number(prediction[key]),
  }));
  const chartData = components.filter((item) => item.value != null);
  const missing = prediction.missingIndicators ?? [];

  return (
    <div className="prediction">
      <div className="prediction__summary">
        <div className="prediction__score">
          <span className="prediction__score-value">
            {prediction.predictedScore == null ? '—' : prediction.predictedScore}
          </span>
          <span className="prediction__score-label">estimated score / 100</span>
        </div>
        <div className="prediction__meta">
          <div className="prediction__meta-row">
            <span className="muted">Predicted grade</span>
            <strong>{prediction.predictedGrade ?? '—'}</strong>
          </div>
          <div className="prediction__meta-row">
            <span className="muted">Risk level</span>
            <StatusBadge variant={RISK_VARIANT[prediction.riskLevel] ?? 'neutral'}>
              {prediction.riskLevel ?? '—'}
            </StatusBadge>
          </div>
          <div className="prediction__meta-row">
            <span className="muted">Generated</span>
            <span>{formatDateTime(prediction.generatedAt)}</span>
          </div>
        </div>
      </div>

      <p className="prediction__note">
        This is a transparent, rule-based <strong>estimate</strong> - a weighted average of the
        indicators below (previous 40%, internals 30%, attendance 15%, assignments 15%),
        not an ML model. Weights are re-normalised over the indicators that have data.
      </p>

      {chartData.length > 0 && (
        <div className="chart">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={chartData} margin={{ top: 8, right: 16, bottom: 8, left: -12 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="label" tick={{ fontSize: 12 }} />
              <YAxis domain={[0, 100]} width={40} />
              <Tooltip formatter={(value) => `${value}/100`} />
              <Bar dataKey="value" radius={[6, 6, 6, 6]}>
                {chartData.map((entry) => (
                  <Cell
                    key={entry.label}
                    fill={entry.value >= 75 ? '#059669' : entry.value >= 50 ? '#d97706' : '#dc2626'}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      <div className="prediction__components">
        {components.map((item) => (
          <div key={item.label} className="prediction__component">
            <span className="prediction__component-label">{item.label}</span>
            <span className="prediction__component-weight">weight {item.weight}</span>
            <span className="prediction__component-value">
              {item.value == null ? 'no data' : item.value}
            </span>
          </div>
        ))}
      </div>

      {missing.length > 0 && (
        <div className="banner banner--warning">
          Missing indicators (excluded from the average): {missing.join(', ')}.
        </div>
      )}

      {prediction.recommendation && (
        <div className="prediction__recommendation">
          <strong>Recommendation.</strong> {prediction.recommendation}
        </div>
      )}
    </div>
  );
}
