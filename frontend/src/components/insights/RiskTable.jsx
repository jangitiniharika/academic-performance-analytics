import StatusBadge from '../common/StatusBadge.jsx';
import { EmptyState } from '../common/StateViews.jsx';

const RISK_VARIANT = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' };

/** Table of students whose predicted performance needs attention. */
export default function RiskTable({ students }) {
  if (!students || students.length === 0) {
    return (
      <EmptyState title="No at-risk students" message="Every student is currently on track." />
    );
  }

  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            <th scope="col">Roll number</th>
            <th scope="col">Name</th>
            <th scope="col">Department</th>
            <th scope="col">Est. score</th>
            <th scope="col">Risk</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student) => (
            <tr key={student.studentId}>
              <td>{student.rollNumber}</td>
              <td>{student.fullName}</td>
              <td>{student.departmentCode ?? '—'}</td>
              <td>{student.predictedScore ?? '—'}</td>
              <td>
                <StatusBadge variant={RISK_VARIANT[student.riskLevel] ?? 'neutral'}>
                  {student.riskLevel}
                </StatusBadge>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
