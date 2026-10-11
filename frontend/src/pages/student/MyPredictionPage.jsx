import useAsync from '../../hooks/useAsync.js';
import { myService } from '../../services/insightService.js';
import { ErrorState, LoadingState } from '../../components/common/StateViews.jsx';
import PredictionPanel from '../../components/insights/PredictionPanel.jsx';

export default function MyPredictionPage() {
  const { status, data, error, reload } = useAsync(() => myService.prediction(), []);

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">My predicted performance</h1>
          <p className="page__subtitle">
            A transparent, rule-based estimate of how you are tracking - not a final grade.
          </p>
        </div>
        <button type="button" className="btn btn--ghost" onClick={reload}>
          Recalculate
        </button>
      </div>

      <div className="card">
        {status === 'loading' && <LoadingState message="Calculating your estimate…" />}
        {status === 'error' && <ErrorState message={error.message} onRetry={reload} />}
        {status === 'success' && <PredictionPanel prediction={data} />}
      </div>
    </section>
  );
}
