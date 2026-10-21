import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { token, login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (token) return <Navigate to="/" replace />;

  async function submit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(username, password);
      navigate('/');
    } catch (loginError) {
      setError('Login failed. Check your username and password.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-screen">
      <form className="login-card" onSubmit={submit}>
        <div className="login-brand"><span className="brand-mark">L</span><span>ledgerly</span></div>
        <p className="eyebrow">WELCOME BACK</p>
        <h1>Your books, in balance.</h1>
        <p className="muted">Sign in to manage your accounting workspace.</p>
        {error && <div className="alert" role="alert">{error}</div>}
        <label>Username<input autoComplete="username" value={username} onChange={(event) => setUsername(event.target.value)} required /></label>
        <label>Password<input type="password" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} required /></label>
        <button className="button button-primary login-submit" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button>
        <p className="login-hint">Demo account: <strong>admin</strong> / <strong>admin123</strong></p>
      </form>
    </main>
  );
}
