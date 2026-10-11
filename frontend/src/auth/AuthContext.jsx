import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';

import { authService } from '../services/authService';

const TOKEN_KEY = 'app.token';
const USER_KEY = 'app.user';

const AuthContext = createContext(null);

function readStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

/**
 * Holds the authenticated user and the JWT for the whole app.
 *
 * The token and a small user summary are persisted in localStorage so a page
 * refresh keeps the session. The token itself is attached to every request by
 * the Axios interceptor in {@code api/client.js}.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [loading, setLoading] = useState(true);

  // Validate a restored session once on start-up.
  useEffect(() => {
    let active = true;
    async function bootstrap() {
      if (!localStorage.getItem(TOKEN_KEY)) {
        setLoading(false);
        return;
      }
      try {
        const me = await authService.me();
        if (active) {
          setUser(me);
          localStorage.setItem(USER_KEY, JSON.stringify(me));
        }
      } catch {
        if (active) {
          localStorage.removeItem(TOKEN_KEY);
          localStorage.removeItem(USER_KEY);
          setUser(null);
          setToken(null);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }
    bootstrap();
    return () => {
      active = false;
    };
  }, []);

  const login = useCallback(async (credentials) => {
    const response = await authService.login(credentials);
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(response.user));
    setToken(response.token);
    setUser(response.user);
    return response.user;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setToken(null);
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({
      user,
      token,
      loading,
      login,
      logout,
      isAuthenticated: Boolean(token && user),
      hasRole: (...roles) => Boolean(user && roles.includes(user.role)),
      dashboardPath: () => {
        if (!user) return '/login';
        return '/app/dashboard';
      },
    }),
    [user, token, loading, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside an <AuthProvider>');
  }
  return context;
}
