import TransactionPage from '../components/TransactionPage';
export default function Purchases() {
  return <TransactionPage title="Purchases" subtitle="Track spending and supplier invoices." endpoint="/purchases" partyLabel="Supplier" partyKey="supplier" />;
}
