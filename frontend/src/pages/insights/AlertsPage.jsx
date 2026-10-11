import { useState } from 'react';

import useOptions from '../../hooks/useOptions.js';
import usePaginatedList from '../../hooks/usePaginatedList.js';
import { alertService } from '../../services/insightService.js';
import { studentService } from '../../services/managementService.js';
import StatusBadge from '../../components/common/StatusBadge.jsx';
import DataTable from '../../components/ui/DataTable.jsx';
import Pagination from '../../components/ui/Pagination.jsx';
import { formatDateTime } from '../../utils/format.js';

const SEVERITY_OPTIONS = [
  { value: 'HIGH', label: 'High' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'LOW', label: 'Low' },
];

const SEVERITY_VARIANT = { LOW: 'info', MEDIUM: 'warning', HIGH: 'danger' };

export default function AlertsPage() {
  const studentOptions = useOptions(studentService.list, (item) => ({
    value: String(item.id),
    label: `${item.rollNumber} — ${item.fullName}`,
  }));

  const [severity, setSeverity] = useState('');
  const [acknowledged, setAcknowledged] = useState('');
  const [banner, setBanner] = useState(null);
  const [generateStudent, setGenerateStudent] = useState('');
  const [generating, setGenerating] = useState(false);

  const list = usePaginatedList(
    (params) =>
      alertService.list({
        ...params,
        severity: severity || undefined,
        acknowledged: acknowledged === '' ? undefined : acknowledged,
      }),
    { size: 10 },
  );

  async function generate() {
    if (!generateStudent) {
      setBanner({ type: 'error', message: 'Select a student first.' });
      return;
    }
    setGenerating(true);
    setBanner(null);
    try {
      const created = await alertService.generate(generateStudent);
      setBanner({
        type: 'success',
        message:
          created.length === 0
            ? 'No new alerts were needed for this student.'
            : `${created.length} new alert(s) created.`,
      });
      list.reload();
    } catch (err) {
      setBanner({ type: 'error', message: err.message });
    } finally {
      setGenerating(false);
    }
  }

  async function acknowledge(row) {
    try {
      await alertService.acknowledge(row.id);
      list.reload();
    } catch (err) {
      setBanner({ type: 'error', message: err.message });
    }
  }

  const columns = [
    {
      key: 'severity',
      header: 'Severity',
      render: (row) => (
        <StatusBadge variant={SEVERITY_VARIANT[row.severity] ?? 'neutral'}>{row.severity}</StatusBadge>
      ),
    },
    { key: 'type', header: 'Type' },
    { key: 'student', header: 'Student', render: (row) => row.student?.fullName ?? '—' },
    { key: 'roll', header: 'Roll number', render: (row) => row.student?.rollNumber ?? '—' },
    { key: 'message', header: 'Message' },
    { key: 'createdAt', header: 'Raised', render: (row) => formatDateTime(row.createdAt) },
    {
      key: 'acknowledged',
      header: 'Status',
      render: (row) =>
        row.acknowledged ? (
          <StatusBadge variant="success">Acknowledged</StatusBadge>
        ) : (
          <StatusBadge variant="warning">Open</StatusBadge>
        ),
    },
  ];

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">Early-warning alerts</h1>
          <p className="page__subtitle">
            Alerts raised when attendance, marks or the prediction estimate need attention.
          </p>
        </div>
      </div>

      {banner && <div className={`banner banner--${banner.type}`}>{banner.message}</div>}

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Re-run alert rules</h2>
            <p className="card__subtitle">
              Evaluates attendance (&lt;75%), marks (&lt;50%) and prediction risk for one student.
            </p>
          </div>
        </div>
        <div className="toolbar">
          <label className="toolbar__field">
            <span className="toolbar__label">Student</span>
            <select
              className="input"
              value={generateStudent}
              onChange={(event) => setGenerateStudent(event.target.value)}
            >
              <option value="">Select a student…</option>
              {studentOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
          <button type="button" className="btn btn--primary" onClick={generate} disabled={generating}>
            {generating ? 'Evaluating…' : 'Generate alerts'}
          </button>
        </div>
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">All alerts</h2>
            <p className="card__subtitle">Filter by severity or acknowledgement state.</p>
          </div>
        </div>

        <div className="toolbar">
          <label className="toolbar__field">
            <span className="toolbar__label">Severity</span>
            <select
              className="input"
              value={severity}
              onChange={(event) => {
                setSeverity(event.target.value);
                list.setFilter({ severity: event.target.value || undefined });
              }}
            >
              <option value="">All</option>
              {SEVERITY_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
          <label className="toolbar__field">
            <span className="toolbar__label">Status</span>
            <select
              className="input"
              value={acknowledged}
              onChange={(event) => {
                setAcknowledged(event.target.value);
                list.setFilter({ acknowledged: event.target.value === '' ? undefined : event.target.value });
              }}
            >
              <option value="">All</option>
              <option value="false">Open</option>
              <option value="true">Acknowledged</option>
            </select>
          </label>
        </div>

        <DataTable
          columns={columns}
          rows={list.rows}
          loading={list.loading}
          error={list.error}
          onRetry={list.reload}
          emptyMessage="No alerts match the current filters."
          renderActions={(row) =>
            row.acknowledged ? (
              <span className="muted">—</span>
            ) : (
              <button type="button" className="btn btn--ghost btn--sm" onClick={() => acknowledge(row)}>
                Acknowledge
              </button>
            )
          }
        />
        <Pagination
          page={list.page}
          totalPages={list.totalPages}
          totalElements={list.totalElements}
          onPageChange={list.setPage}
        />
      </div>
    </section>
  );
}
