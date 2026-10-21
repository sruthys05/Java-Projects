package com.financial.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JournalRequest(LocalDate date, String description, BigDecimal debit, BigDecimal credit) {}
