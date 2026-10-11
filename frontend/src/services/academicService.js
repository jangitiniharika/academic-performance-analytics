import apiClient from '../api/client';

/** Same five REST operations as the management resources. */
const crud = (base) => ({
  list: async (params) => (await apiClient.get(base, { params })).data,
  get: async (id) => (await apiClient.get(`${base}/${id}`)).data,
  create: async (payload) => (await apiClient.post(base, payload)).data,
  update: async (id, payload) => (await apiClient.put(`${base}/${id}`, payload)).data,
  remove: async (id) => {
    await apiClient.delete(`${base}/${id}`);
  },
});

// --- Stages 5-8: academic records ------------------------------------------
export const examService = crud('/exams');
export const marksService = crud('/marks');
export const assignmentService = crud('/assignments');
export const submissionService = crud('/submissions');
export const resultService = crud('/results');

export const attendanceService = {
  ...crud('/attendance'),
  percentage: async (studentId) => (await apiClient.get(`/attendance/percentage/${studentId}`)).data,
};

export const cgpaService = {
  get: async (studentId) => (await apiClient.get(`/results/cgpa/${studentId}`)).data,
};
