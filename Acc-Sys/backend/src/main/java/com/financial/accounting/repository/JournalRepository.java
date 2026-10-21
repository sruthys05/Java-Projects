package com.financial.accounting.repository;

import com.financial.accounting.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {}
