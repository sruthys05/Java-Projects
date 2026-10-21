import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';

const money = (value) => Number(value || 0).toLocaleString(undefined, { style: 'currency', currency: 'USD' });

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => {
    api.get('/dashboard').then(setSummary).catch((requestError) => setError(requestError.message));
  }, []);

  return (
    <section>
      <div className="page-heading">
        <div><p className="eyebrow">YOUR BUSINESS AT A GLANCE</p><h1>Good to see you.</h1><p className="muted">Here is a simple view of your recorded finances.</p></div>
        <Link className="button button-primary" to="/sales">＋ Add a sale</Link>
      </div>
      {error && <div className="alert" role="alert">{error}</div>}
      {!summary && !error ? <div className="panel loading">Loading your summary…</div> : summary && (
        <>
          <div className="stat-grid">
            <article className="stat-card accent-green"><span className="stat-label">Total sales</span><strong>{money(summary.totalSales)}</strong><span className="stat-foot">{summary.salesCount} recorded sales</span></article>
            <article className="stat-card accent-orange"><span className="stat-label">Total purchases</span><strong>{money(summary.totalPurchases)}</strong><span className="stat-foot">{summary.purchaseCount} recorded purchases</span></article>
            <article className="stat-card accent-blue"><span className="stat-label">Money received</span><strong>{money(summary.totalReceipts)}</strong><span className="stat-foot">{summary.receiptCount} receipts</span></article>
            <article className="stat-card accent-purple"><span className="stat-label">Money paid</span><strong>{money(summary.totalPayments)}</strong><span className="stat-foot">{summary.paymentCount} payments</span></article>
          </div>
          <div className="dashboard-bottom">
            <article className="panel income-panel"><p className="eyebrow">QUICK SUMMARY</p><h2>Sales minus purchases</h2><p className="muted">A simple income estimate based on records entered so far.</p><strong className="income-value">{money(summary.netIncome)}</strong><Link to="/reports" className="text-link">View reports →</Link></article>
            <article className="panel quick-links"><p className="eyebrow">GET STARTED</p><h2>Keep your books up to date</h2><Link to="/purchases">＋ Record a purchase</Link><Link to="/receipts">＋ Add a receipt</Link><Link to="/journals">＋ Create a journal entry</Link></article>
          </div>
        </>
      )}
    </section>
  );
}
