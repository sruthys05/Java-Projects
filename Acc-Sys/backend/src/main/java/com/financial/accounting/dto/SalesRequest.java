package com.financial.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesRequest(LocalDate date, String customer, String description, BigDecimal amount) {}
