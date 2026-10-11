import { useState } from 'react';

import useOptions from '../../hooks/useOptions.js';
import { predictionService } from '../../services/insightService.js';
import { studentService } from '../../services/managementService.js';
import { EmptyState } from '../../components/common/StateViews.jsx';
import PredictionPanel from '../../components/insights/PredictionPanel.jsx';

export default function PredictionsPage() {
  const studentOptions = useOptions(studentService.list, (item) => ({
    value: String(item.id),
    label: `${item.rollNumber} — ${item.fullName}`,
  }));

  const [studentId, setStudentId] = useState('');
  const [prediction, setPrediction] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  async function runPrediction() {
    if (!studentId) {
      setError('Select a student first.');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const result = await predictionService.generate(studentId);
      setPrediction(result);
    } catch (err) {
      setError(err.message || 'Could not generate a prediction.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">Performance prediction</h1>
          <p className="page__subtitle">
            Run the transparent, rule-based estimate for a student and explain every component.
          </p>
        </div>
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Run a prediction</h2>
            <p className="card__subtitle">
              Previous results 40% · internal marks 30% · attendance 15% · assignments 15%.
            </p>
          </div>
        </div>
        <div className="toolbar">
          <label className="toolbar__field">
            <span className="toolbar__label">Student</span>
            <select
              className="input"
              value={studentId}
              onChange={(event) => setStudentId(event.target.value)}
            >
              <option value="">Select a student…</option>
              {studentOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
          <button type="button" className="btn btn--primary" onClick={runPrediction} disabled={loading}>
            {loading ? 'Estimating…' : 'Run prediction'}
          </button>
        </div>
        {error && <div className="banner banner--error">{error}</div>}
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Result</h2>
            <p className="card__subtitle">Estimated outcome and the inputs behind it.</p>
          </div>
        </div>
        {prediction ? (
          <PredictionPanel prediction={prediction} />
        ) : (
          <EmptyState
            title="No prediction yet"
            message="Select a student and run a prediction to see the explanation."
          />
        )}
      </div>
    </section>
  );
}
