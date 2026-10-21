import TransactionPage from '../components/TransactionPage';
export default function Sales() {
  return <TransactionPage title="Sales" subtitle="Keep a clear record of customer sales." endpoint="/sales" partyLabel="Customer" partyKey="customer" />;
}
