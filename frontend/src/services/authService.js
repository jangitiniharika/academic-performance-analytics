import apiClient from '../api/client';

/** Authentication calls (Stage 3/14). */
export const authService = {
  login: async (credentials) => (await apiClient.post('/auth/login', credentials)).data,
  register: async (payload) => (await apiClient.post('/auth/register', payload)).data,
  me: async () => (await apiClient.get('/auth/me')).data,
};
