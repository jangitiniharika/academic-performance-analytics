import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';

import App from './App.jsx';
import { AuthProvider } from './auth/AuthContext.jsx';
import './index.css';

/**
 * Application bootstrap.
 *
 * `AuthProvider` restores the JWT session and exposes the signed-in user to
 * every screen; `BrowserRouter` enables client-side routing.
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
);
