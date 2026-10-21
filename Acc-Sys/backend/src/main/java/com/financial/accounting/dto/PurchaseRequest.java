package com.financial.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseRequest(LocalDate date, String supplier, String description, BigDecimal amount) {}
