import { Navigate, Route, Routes } from 'react-router-dom';

import AppLayout from './components/layout/AppLayout.jsx';
import DashboardLayout from './components/layout/DashboardLayout.jsx';
import ProtectedRoute from './routes/ProtectedRoute.jsx';

import LandingPage from './pages/LandingPage.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';
import ForbiddenPage from './pages/ForbiddenPage.jsx';
import LoginPage from './pages/auth/LoginPage.jsx';
import RegisterPage from './pages/auth/RegisterPage.jsx';

import RoleDashboardPage from './pages/dashboards/RoleDashboardPage.jsx';
import ProfilePage from './pages/ProfilePage.jsx';

import DepartmentsPage from './pages/management/DepartmentsPage.jsx';
import SubjectsPage from './pages/management/SubjectsPage.jsx';
import StudentsPage from './pages/management/StudentsPage.jsx';
import FacultyPage from './pages/management/FacultyPage.jsx';

import AttendancePage from './pages/academics/AttendancePage.jsx';
import ExamsPage from './pages/academics/ExamsPage.jsx';
import MarksPage from './pages/academics/MarksPage.jsx';
import AssignmentsPage from './pages/academics/AssignmentsPage.jsx';
import SubmissionsPage from './pages/academics/SubmissionsPage.jsx';
import ResultsPage from './pages/academics/ResultsPage.jsx';

import AnalyticsPage from './pages/insights/AnalyticsPage.jsx';
import PredictionsPage from './pages/insights/PredictionsPage.jsx';
import AlertsPage from './pages/insights/AlertsPage.jsx';

import MyAttendancePage from './pages/student/MyAttendancePage.jsx';
import MyMarksPage from './pages/student/MyMarksPage.jsx';
import MyAssignmentsPage from './pages/student/MyAssignmentsPage.jsx';
import MyResultsPage from './pages/student/MyResultsPage.jsx';
import MyPredictionPage from './pages/student/MyPredictionPage.jsx';

const ADMIN = ['ADMIN'];
const STAFF = ['ADMIN', 'FACULTY'];
const STUDENT = ['STUDENT'];

/**
 * Route table.
 *
 * Public pages live under the marketing shell (`AppLayout`). Everything under
 * `/app/**` requires a signed-in user (`ProtectedRoute`) and is rendered inside
 * the role-aware sidebar shell (`DashboardLayout`). Each section is then guarded
 * by the roles allowed to see it.
 */
export default function App() {
  return (
    <Routes>
      {/* Public */}
      <Route element={<AppLayout />}>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forbidden" element={<ForbiddenPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>

      {/* Authenticated application shell */}
      <Route element={<ProtectedRoute />}>
        <Route element={<DashboardLayout />}>
          <Route path="/app" element={<RoleDashboardPage />} />
          <Route path="/app/dashboard" element={<RoleDashboardPage />} />
          <Route path="/app/profile" element={<ProfilePage />} />

          {/* Management: faculty may READ students and subjects (read-only). */}
          <Route element={<ProtectedRoute roles={STAFF} />}>
            <Route path="/app/students" element={<StudentsPage />} />
            <Route path="/app/subjects" element={<SubjectsPage />} />
          </Route>

          {/* Admin-only management */}
          <Route element={<ProtectedRoute roles={ADMIN} />}>
            <Route path="/app/departments" element={<DepartmentsPage />} />
            <Route path="/app/faculty" element={<FacultyPage />} />
          </Route>

          {/* Admin + Faculty academics & insights */}
          <Route element={<ProtectedRoute roles={STAFF} />}>
            <Route path="/app/attendance" element={<AttendancePage />} />
            <Route path="/app/exams" element={<ExamsPage />} />
            <Route path="/app/marks" element={<MarksPage />} />
            <Route path="/app/assignments" element={<AssignmentsPage />} />
            <Route path="/app/submissions" element={<SubmissionsPage />} />
            <Route path="/app/results" element={<ResultsPage />} />
            <Route path="/app/analytics" element={<AnalyticsPage />} />
            <Route path="/app/predictions" element={<PredictionsPage />} />
            <Route path="/app/alerts" element={<AlertsPage />} />
          </Route>

          {/* Student self-service */}
          <Route element={<ProtectedRoute roles={STUDENT} />}>
            <Route path="/app/my/attendance" element={<MyAttendancePage />} />
            <Route path="/app/my/marks" element={<MyMarksPage />} />
            <Route path="/app/my/assignments" element={<MyAssignmentsPage />} />
            <Route path="/app/my/results" element={<MyResultsPage />} />
            <Route path="/app/my/prediction" element={<MyPredictionPage />} />
          </Route>
        </Route>
      </Route>
    </Routes>
  );
}
