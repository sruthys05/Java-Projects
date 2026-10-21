import { useEffect, useState } from 'react';
import { api } from '../services/api';

const money = (value) => Number(value || 0).toLocaleString(undefined, { style: 'currency', currency: 'USD' });

export default function Reports() {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => {
    api.get('/dashboard').then(setSummary).catch((requestError) => setError(requestError.message));
  }, []);
  return (
    <section>
      <div className="page-heading"><div><p className="eyebrow">FINANCIAL OVERVIEW</p><h1>Reports</h1><p className="muted">A high-level view of the records entered in your books.</p></div></div>
      {error && <div className="alert" role="alert">{error}</div>}
      {!summary && !error ? <div className="panel loading">Preparing your report…</div> : summary && (
        <div className="report-layout">
          <article className="panel report-card"><span className="eyebrow">BASIC PROFIT &amp; LOSS</span><h2>Income estimate</h2><div className="report-line"><span>Sales</span><strong>{money(summary.totalSales)}</strong></div><div className="report-line"><span>Purchases</span><strong>− {money(summary.totalPurchases)}</strong></div><div className="report-line report-total"><span>Sales minus purchases</span><strong>{money(summary.netIncome)}</strong></div><p className="report-note">This is a simple estimate, not a formal financial statement. It excludes taxes and other expenses.</p></article>
          <article className="panel report-card"><span className="eyebrow">CASH ACTIVITY</span><h2>Receipts &amp; payments</h2><div className="report-line"><span>Receipts recorded</span><strong>{money(summary.totalReceipts)}</strong></div><div className="report-line"><span>Payments recorded</span><strong>− {money(summary.totalPayments)}</strong></div><div className="report-line report-total"><span>Net cash recorded</span><strong>{money(Number(summary.totalReceipts) - Number(summary.totalPayments))}</strong></div><p className="report-note">Totals include all records currently saved in your workspace.</p></article>
        </div>
      )}
    </section>
  );
}
