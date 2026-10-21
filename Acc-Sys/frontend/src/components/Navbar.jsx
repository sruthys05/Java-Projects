import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { username, logout } = useAuth();
  return (
    <header className="topbar">
      <span className="topbar-caption">FINANCE WORKSPACE</span>
      <div className="user-menu">
        <span className="avatar">{username.charAt(0).toUpperCase()}</span>
        <span className="username">{username}</span>
        <button className="button button-light button-small" onClick={logout}>Sign out</button>
      </div>
    </header>
  );
}
