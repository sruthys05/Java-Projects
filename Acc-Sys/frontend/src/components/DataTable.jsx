export default function DataTable({ columns, rows, onDelete, onEdit, onView }) {
  if (!rows.length) return <div className="empty-state">No entries yet. Add your first record above.</div>;

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>{columns.map((column) => <th key={column.key}>{column.label}</th>)}<th /></tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.id}>
              {columns.map((column) => (
                <td key={column.key}>{column.format ? column.format(row[column.key]) : row[column.key]}</td>
              ))}
              <td className="action-cell">
                {onView && <button className="edit-button" onClick={() => onView(row)} aria-label="View receipt">View</button>}
                {onEdit && <button className="edit-button" onClick={() => onEdit(row)} aria-label="Edit entry">Edit</button>}
                <button className="delete-button" onClick={() => onDelete(row.id)} aria-label="Delete entry">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
