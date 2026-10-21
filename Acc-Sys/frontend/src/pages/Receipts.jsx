import TransactionPage from '../components/TransactionPage';
export default function Receipts() {
  return <TransactionPage title="Receipts" subtitle="Record money received by your business." endpoint="/receipts" partyLabel="Received from" partyKey="fromName" showReceipt />;
}
