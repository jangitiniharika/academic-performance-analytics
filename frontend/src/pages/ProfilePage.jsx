import { useAuth } from '../auth/AuthContext.jsx';
import useAsync from '../hooks/useAsync.js';
import { myService } from '../services/insightService.js';
import { ErrorState, LoadingState } from '../components/common/StateViews.jsx';

/** Short description of what each role is allowed to do. */
const ROLE_INFO = {
  ADMIN: 'Full access: manage the institution and every academic record.',
  FACULTY: 'Read students and subjects, and manage attendance, exams, marks, '
    + 'assignments, results, analytics and alerts for your department.',
  STUDENT: 'Read-only access to your own attendance, marks, assignments, '
    + 'results and predicted performance.',
};

/**
 * Profile screen shown to every signed-in role.
 *
 * <p>It never displays credentials: the account block only shows the details the
 * user already owns, and a STUDENT additionally sees the academic record linked to
 * their login (served by {@code /api/student/me}).
 */
export default function ProfilePage() {
  const { user } = useAuth();
  const isStudent = user?.role === 'STUDENT';

  const profile = useAsync(
    () => (isStudent ? myService.profile() : Promise.resolve(null)),
    [isStudent],
  );

  if (profile.status === 'loading') {
    return <LoadingState message="Loading profile…" />;
  }
  if (profile.status === 'error') {
    return <ErrorState message={profile.error.message} onRetry={profile.reload} />;
  }

  const student = profile.data;

  return (
    <section className="page">
      <div className="page__head">
        <div>
          <h1 className="page__title">My profile</h1>
          <p className="page__subtitle">Your sign-in account and the role it grants you.</p>
        </div>
      </div>

      <div className="card">
        <div className="card__header">
          <div>
            <h2 className="card__title">Account</h2>
            <p className="card__subtitle">
              Passwords are never displayed — only your profile details.
            </p>
          </div>
        </div>
        <dl className="profile-list">
          <ProfileRow label="Username" value={user?.username} />
          <ProfileRow label="Email" value={user?.email} />
          <ProfileRow
            label="Role"
            value={<span className="badge badge--info">{user?.role}</span>}
          />
          <ProfileRow label="Status" value={user?.enabled === false ? 'Disabled' : 'Active'} />
        </dl>
      </div>

      {isStudent && student && (
        <div className="card">
          <div className="card__header">
            <div>
              <h2 className="card__title">Student record</h2>
              <p className="card__subtitle">The academic profile linked to your account.</p>
            </div>
          </div>
          <dl className="profile-list">
            <ProfileRow label="Roll number" value={student.rollNumber} />
            <ProfileRow label="Registration number" value={student.registrationNumber} />
            <ProfileRow label="Name" value={student.fullName} />
            <ProfileRow
              label="Department"
              value={student.department?.name ?? student.department?.code}
            />
            <ProfileRow label="Current semester" value={student.currentSemester} />
            <ProfileRow label="CGPA" value={student.cgpa ?? '—'} />
          </dl>
        </div>
      )}

      {!isStudent && (
        <div className="card">
          <div className="card__header">
            <div>
              <h2 className="card__title">What this role can do</h2>
            </div>
          </div>
          <p className="page__subtitle">{ROLE_INFO[user?.role]}</p>
        </div>
      )}
    </section>
  );
}

function ProfileRow({ label, value }) {
  return (
    <div className="profile-list__row">
      <dt className="profile-list__label">{label}</dt>
      <dd className="profile-list__value">{value ?? '—'}</dd>
    </div>
  );
}
