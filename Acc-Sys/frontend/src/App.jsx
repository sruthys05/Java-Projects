import { Navigate, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Journals from './pages/Journals';
import Login from './pages/Login';
import Payments from './pages/Payments';
import Purchases from './pages/Purchases';
import Receipts from './pages/Receipts';
import Reports from './pages/Reports';
import Sales from './pages/Sales';

function Workspace() {
  return (
    <div className="app-shell">
      <Sidebar />
      <div className="workspace">
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/purchases" element={<Purchases />} />
            <Route path="/sales" element={<Sales />} />
            <Route path="/receipts" element={<Receipts />} />
            <Route path="/payments" element={<Payments />} />
            <Route path="/journals" element={<Journals />} />
            <Route path="/reports" element={<Reports />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/*" element={<ProtectedRoute><Workspace /></ProtectedRoute>} />
    </Routes>
  );
}
