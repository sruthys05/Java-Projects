import { NavLink } from 'react-router-dom';

const links = [
  { to: '/', label: 'Overview', icon: '◫' },
  { to: '/purchases', label: 'Purchases', icon: '↓' },
  { to: '/sales', label: 'Sales', icon: '↑' },
  { to: '/receipts', label: 'Receipts', icon: '＋' },
  { to: '/payments', label: 'Payments', icon: '−' },
  { to: '/journals', label: 'Journal entries', icon: '≡' },
  { to: '/reports', label: 'Reports', icon: '▤' },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <NavLink className="brand" to="/">
        <span className="brand-mark">L</span>
        <span>ledgerly<small>ACCOUNTING</small></span>
      </NavLink>
      <p className="nav-label">WORKSPACE</p>
      <nav>
        {links.map((link) => (
          <NavLink key={link.to} to={link.to} end={link.to === '/'} className="nav-link">
            <span className="nav-icon">{link.icon}</span>{link.label}
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-note">Simple books.<br />Clear decisions.</div>
    </aside>
  );
}
