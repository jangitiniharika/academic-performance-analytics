import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

/**
 * Vite configuration.
 *
 * The React app talks to Spring Boot through the base URL defined in `.env`
 * (VITE_API_BASE_URL). The dev server runs on port 5173, which is exactly the
 * origin allowed by the backend CORS configuration.
 */
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    open: false,
  },
  preview: {
    port: 4173,
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
  },
});
