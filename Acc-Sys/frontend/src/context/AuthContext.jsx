import { createContext, useContext, useMemo, useState } from 'react';
import { api } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [username, setUsername] = useState(() => localStorage.getItem('username') || '');

  async function login(usernameInput, password) {
    const result = await api.post('/auth/login', { username: usernameInput, password });
    localStorage.setItem('token', result.token);
    localStorage.setItem('username', result.username);
    setToken(result.token);
    setUsername(result.username);
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setToken(null);
    setUsername('');
  }

  const value = useMemo(() => ({ token, username, login, logout }), [token, username]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
