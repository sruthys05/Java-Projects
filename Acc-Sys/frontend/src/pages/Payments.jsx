import TransactionPage from '../components/TransactionPage';
export default function Payments() {
  return <TransactionPage title="Payments" subtitle="Record outgoing business payments." endpoint="/payments" partyLabel="Paid to" partyKey="toName" />;
}
