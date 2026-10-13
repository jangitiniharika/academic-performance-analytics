import apiClient from '../api/client';

// --- Stage 9: analytics -----------------------------------------------------
export const analyticsService = {
  overview: async () => (await apiClient.get('/analytics/overview')).data,
  student: async (studentId) => (await apiClient.get(`/analytics/student/${studentId}`)).data,
  classAnalytics: async (subjectId) => (await apiClient.get(`/analytics/class/${subjectId}`)).data,
};

// --- Stage 10: prediction ---------------------------------------------------
export const predictionService = {
  generate: async (studentId) => (await apiClient.post(`/predictions/generate/${studentId}`)).data,
  latest: async (studentId) => (await apiClient.get(`/predictions/student/${studentId}`)).data,
};

// --- Stage 11: early-warning alerts ----------------------------------------
export const alertService = {
  list: async (params) => (await apiClient.get('/alerts', { params })).data,
  forStudent: async (studentId) => (await apiClient.get(`/alerts/student/${studentId}`)).data,
  generate: async (studentId) => (await apiClient.post(`/alerts/generate/${studentId}`)).data,
  acknowledge: async (id) => (await apiClient.patch(`/alerts/${id}/acknowledge`)).data,
};

// --- Stages 16-17: dashboards ----------------------------------------------
export const dashboardService = {
  admin: async () => (await apiClient.get('/dashboard/admin')).data,
  faculty: async () => (await apiClient.get('/dashboard/faculty')).data,
  student: async () => (await apiClient.get('/student/dashboard')).data,
};

// --- Student self-service (Stage 15) ---------------------------------------
export const myService = {
  profile: async () => (await apiClient.get('/student/me')).data,
  dashboard: async () => (await apiClient.get('/student/dashboard')).data,
  attendance: async (params) => (await apiClient.get('/student/attendance', { params })).data,
  attendancePercentage: async () => (await apiClient.get('/student/attendance/percentage')).data,
  marks: async (params) => (await apiClient.get('/student/marks', { params })).data,
  assignments: async (params) => (await apiClient.get('/student/assignments', { params })).data,
  results: async (params) => (await apiClient.get('/student/results', { params })).data,
  cgpa: async () => (await apiClient.get('/student/cgpa')).data,
  prediction: async () => (await apiClient.get('/student/prediction')).data,
  alerts: async () => (await apiClient.get('/student/alerts')).data,
  analytics: async () => (await apiClient.get('/student/analytics')).data,
};
