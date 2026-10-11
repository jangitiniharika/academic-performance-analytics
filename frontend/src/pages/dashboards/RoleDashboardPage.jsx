import { useAuth } from '../../auth/AuthContext.jsx';
import AdminDashboardPage from './AdminDashboardPage.jsx';
import FacultyDashboardPage from './FacultyDashboardPage.jsx';
import StudentDashboardPage from './StudentDashboardPage.jsx';

/** Renders the dashboard that matches the signed-in user's role. */
export default function RoleDashboardPage() {
  const { user } = useAuth();

  if (user?.role === 'ADMIN') {
    return <AdminDashboardPage />;
  }
  if (user?.role === 'FACULTY') {
    return <FacultyDashboardPage />;
  }
  return <StudentDashboardPage />;
}
