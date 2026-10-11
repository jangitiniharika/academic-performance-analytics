import apiClient from '../api/client';

/**
 * Builds the standard REST client for one resource.
 *
 * Every management resource (departments, subjects, students, faculty) exposes
 * the same five operations, so one factory keeps the pages small and consistent.
 */
const crud = (base) => ({
  list: async (params) => (await apiClient.get(base, { params })).data,
  get: async (id) => (await apiClient.get(`${base}/${id}`)).data,
  create: async (payload) => (await apiClient.post(base, payload)).data,
  update: async (id, payload) => (await apiClient.put(`${base}/${id}`, payload)).data,
  remove: async (id) => {
    await apiClient.delete(`${base}/${id}`);
  },
});

// --- Stage 4: management resources -----------------------------------------
export const departmentService = crud('/departments');
export const subjectService = crud('/subjects');
export const studentService = crud('/students');
export const facultyService = crud('/faculty');
