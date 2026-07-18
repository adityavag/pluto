import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import * as authApi from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Rehydrate from localStorage on mount
  useEffect(() => {
    const savedToken = localStorage.getItem('pluto_token');
    const savedUser = localStorage.getItem('pluto_user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      try {
        setUser(JSON.parse(savedUser));
      } catch {
        localStorage.removeItem('pluto_user');
      }
    }
    setLoading(false);
  }, []);

  const login = useCallback(async ({ email, password }) => {
    const data = await authApi.login({ email, password });
    const jwt = data.token;
    const userData = data.user;
    localStorage.setItem('pluto_token', jwt);
    localStorage.setItem('pluto_user', JSON.stringify(userData));
    setToken(jwt);
    setUser(userData);
    return data;
  }, []);

  const register = useCallback(async ({ username, email, password }) => {
    const data = await authApi.register({ username, email, password });
    return data;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('pluto_token');
    localStorage.removeItem('pluto_user');
    setToken(null);
    setUser(null);
  }, []);

  const value = {
    user,
    token,
    isAuthenticated: !!token,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

export default AuthContext;
