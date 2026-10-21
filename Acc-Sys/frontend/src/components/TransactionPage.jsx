import { useEffect, useState } from 'react';
import { api } from '../services/api';
import DataTable from './DataTable';

export default function TransactionPage({ title, subtitle, endpoint, partyLabel, partyKey, showReceipt = false }) {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [viewingReceipt, setViewingReceipt] = useState(null);
  const [form, setForm] = useState({ date: new Date().toISOString().slice(0, 10), party: '', description: '', amount: '' });

  async function loadRows() {
    try {
      setRows(await api.get(endpoint));
      setError('');
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  useEffect(() => { loadRows(); }, [endpoint]);

  function updateField(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function saveEntry(event) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      const entry = {
        date: form.date,
        [partyKey]: form.party.trim(),
        description: form.description.trim(),
        amount: Number(form.amount),
      };
      const savedEntry = editingId
        ? await api.put(`${endpoint}/${editingId}`, entry)
        : await api.post(endpoint, entry);
      if (showReceipt) setViewingReceipt(savedEntry);
      setEditingId(null);
      setForm({ date: new Date().toISOString().slice(0, 10), party: '', description: '', amount: '' });
      await loadRows();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSaving(false);
    }
  }

  function editEntry(row) {
    setEditingId(row.id);
    setForm({
      date: row.date,
      party: row[partyKey],
      description: row.description,
      amount: row.amount,
    });
  }

  function cancelEdit() {
    setEditingId(null);
    setForm({ date: new Date().toISOString().slice(0, 10), party: '', description: '', amount: '' });
  }

  async function deleteEntry(id) {
    if (!window.confirm('Delete this record?')) return;
    try {
      await api.delete(`${endpoint}/${id}`);
      await loadRows();
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  const money = (value) => value == null ? '—' : Number(value).toLocaleString(undefined, { style: 'currency', currency: 'USD' });
  const columns = [
    { key: 'date', label: 'Date' },
    { key: partyKey, label: partyLabel },
    { key: 'description', label: 'Description' },
    { key: 'amount', label: 'Amount', format: money },
  ];

  return (
    <section>
      <div className="page-heading">
        <div><p className="eyebrow">BOOKKEEPING</p><h1>{title}</h1><p className="muted">{subtitle}</p></div>
        <span className="record-count">{rows.length} {rows.length === 1 ? 'record' : 'records'}</span>
      </div>
      <form className="entry-form" onSubmit={saveEntry}>
        <label>Date<input type="date" name="date" value={form.date} onChange={updateField} required /></label>
        <label>{partyLabel}<input name="party" value={form.party} onChange={updateField} placeholder={partyLabel} maxLength="100" required /></label>
        <label className="description-field">Description<input name="description" value={form.description} onChange={updateField} placeholder="What is this for?" maxLength="250" required /></label>
        <label>Amount<input type="number" name="amount" value={form.amount} onChange={updateField} min="0.01" step="0.01" placeholder="0.00" required /></label>
        <button className="button button-primary" disabled={saving}>{saving ? 'Saving…' : editingId ? 'Save changes' : 'Add entry'}</button>
        {editingId && <button type="button" className="button button-light" onClick={cancelEdit}>Cancel</button>}
      </form>
      {error && <div className="alert" role="alert">{error}</div>}
      <div className="panel"><DataTable columns={columns} rows={rows} onDelete={deleteEntry} onEdit={editEntry} onView={showReceipt ? setViewingReceipt : undefined} /></div>
      {viewingReceipt && (
        <div className="receipt-modal-backdrop" role="presentation" onClick={() => setViewingReceipt(null)}>
          <section className="receipt-dialog" role="dialog" aria-modal="true" aria-labelledby="receipt-title" onClick={(event) => event.stopPropagation()}>
            <div className="receipt-actions">
              <button className="button button-light" onClick={() => setViewingReceipt(null)}>Close</button>
              <button className="button button-primary" onClick={() => window.print()}>Print receipt</button>
            </div>
            <article className="receipt-paper">
              <p className="eyebrow">PAYMENT RECEIPT</p>
              <h2 id="receipt-title">Receipt #{viewingReceipt.id}</h2>
              <p className="muted">Proof of payment received</p>
              <div className="receipt-details">
                <div><span>Receipt number</span><strong>RCP-{viewingReceipt.id}</strong></div>
                <div><span>Date</span><strong>{viewingReceipt.date}</strong></div>
                <div><span>Received from</span><strong>{viewingReceipt[partyKey]}</strong></div>
                <div><span>Description</span><strong>{viewingReceipt.description}</strong></div>
              </div>
              <div className="receipt-total"><span>Amount received</span><strong>{money(viewingReceipt.amount)}</strong></div>
              <p className="receipt-thanks">Thank you for your payment.</p>
            </article>
          </section>
        </div>
      )}
    </section>
  );
}
