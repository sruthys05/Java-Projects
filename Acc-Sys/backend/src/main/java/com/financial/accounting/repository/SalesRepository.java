package com.financial.accounting.repository;

import com.financial.accounting.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, Long> {}
