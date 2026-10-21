package com.financial.accounting.service;

import com.financial.accounting.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class DashboardService {
    private final PurchaseRepository purchases;
    private final SalesRepository sales;
    private final ReceiptRepository receipts;
    private final PaymentRepository payments;

    public DashboardService(PurchaseRepository purchases, SalesRepository sales,
                            ReceiptRepository receipts, PaymentRepository payments) {
        this.purchases = purchases;
        this.sales = sales;
        this.receipts = receipts;
        this.payments = payments;
    }

    public Map<String, Object> summary() {
        BigDecimal totalSales = sales.findAll().stream().map(item -> item.getAmount())
                .filter(amount -> amount != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPurchases = purchases.findAll().stream().map(item -> item.getAmount())
                .filter(amount -> amount != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReceipts = receipts.findAll().stream().map(item -> item.getAmount())
                .filter(amount -> amount != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPayments = payments.findAll().stream().map(item -> item.getAmount())
                .filter(amount -> amount != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "totalSales", totalSales,
                "totalPurchases", totalPurchases,
                "totalReceipts", totalReceipts,
                "totalPayments", totalPayments,
                "netIncome", totalSales.subtract(totalPurchases),
                "salesCount", sales.count(),
                "purchaseCount", purchases.count(),
                "receiptCount", receipts.count(),
                "paymentCount", payments.count());
    }
}
