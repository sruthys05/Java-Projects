import { useEffect, useState } from 'react';
import { api } from '../services/api';
import DataTable from '../components/DataTable';

export default function Journals() {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({ date: new Date().toISOString().slice(0, 10), description: '', debit: '', credit: '' });
  const [error, setError] = useState('');
  const [editingId, setEditingId] = useState(null);
  async function loadRows() {
    try { setRows(await api.get('/journals')); setError(''); }
    catch (requestError) { setError(requestError.message); }
  }
  useEffect(() => { loadRows(); }, []);
  function update(event) { setForm({ ...form, [event.target.name]: event.target.value }); }
  async function save(event) {
    event.preventDefault();
    setError('');
    try {
      const entry = { ...form, debit: Number(form.debit || 0), credit: Number(form.credit || 0) };
      if (editingId) await api.put(`/journals/${editingId}`, entry);
      else await api.post('/journals', entry);
      setEditingId(null);
      setForm({ date: new Date().toISOString().slice(0, 10), description: '', debit: '', credit: '' });
      await loadRows();
    } catch (requestError) { setError(requestError.message); }
  }
  function edit(row) {
    setEditingId(row.id);
    setForm({ date: row.date, description: row.description, debit: row.debit, credit: row.credit });
  }
  function cancelEdit() {
    setEditingId(null);
    setForm({ date: new Date().toISOString().slice(0, 10), description: '', debit: '', credit: '' });
  }
  async function remove(id) {
    if (!window.confirm('Delete this journal entry?')) return;
    try { await api.delete(`/journals/${id}`); await loadRows(); }
    catch (requestError) { setError(requestError.message); }
  }
  const money = (value) => Number(value || 0).toLocaleString(undefined, { style: 'currency', currency: 'USD' });
  const columns = [
    { key: 'date', label: 'Date' },
    { key: 'description', label: 'Description' },
    { key: 'debit', label: 'Debit', format: money },
    { key: 'credit', label: 'Credit', format: money },
  ];
  return (
    <section>
      <div className="page-heading"><div><p className="eyebrow">BOOKKEEPING</p><h1>Journal entries</h1><p className="muted">Record debit and credit adjustments.</p></div><span className="record-count">{rows.length} records</span></div>
      <form className="entry-form journal-form" onSubmit={save}>
        <label>Date<input type="date" name="date" value={form.date} onChange={update} required /></label>
        <label className="description-field">Description<input name="description" value={form.description} onChange={update} maxLength="250" required /></label>
        <label>Debit<input type="number" min="0" step="0.01" name="debit" value={form.debit} onChange={update} /></label>
        <label>Credit<input type="number" min="0" step="0.01" name="credit" value={form.credit} onChange={update} /></label>
        <button className="button button-primary">{editingId ? 'Save changes' : 'Add entry'}</button>
        {editingId && <button type="button" className="button button-light" onClick={cancelEdit}>Cancel</button>}
      </form>
      {error && <div className="alert" role="alert">{error}</div>}
      <div className="panel"><DataTable columns={columns} rows={rows} onDelete={remove} onEdit={edit} /></div>
    </section>
  );
}
