import apiClient from '../api/client';

/**
 * Health check against the Spring Boot backend.
 * Returns: { status, application, timestamp, database: { status, product } }
 */
export async function fetchHealth() {
  const { data } = await apiClient.get('/health');
  return data;
}
