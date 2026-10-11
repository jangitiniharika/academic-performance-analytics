import axios from 'axios';

/**
 * Single shared Axios instance for the whole application.
 *
 * - The backend URL comes from the environment variable VITE_API_BASE_URL,
 *   never from a hard-coded value (only the localhost default is a fallback).
 * - The request interceptor attaches the JWT once Stage 3 delivers one.
 * - The response interceptor converts any failure into a simple, predictable
 *   object { status, message, fieldErrors } so components never touch raw
 *   Axios internals.
 */
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// --- Request: attach the bearer token if the user is logged in (Stage 3+) ---
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('app.token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// --- Response: normalise errors into a friendly shape ---
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const payload = error.response?.data;
    const normalised = {
      status: error.response?.status ?? 0,
      message:
        payload?.message ||
        (error.code === 'ECONNABORTED'
          ? 'The server took too long to respond.'
          : error.message) ||
        'Something went wrong. Please try again.',
      fieldErrors: payload?.fieldErrors ?? null,
    };
    return Promise.reject(normalised);
  },
);

export default apiClient;
