package com.financial.accounting.service;

import com.financial.accounting.entity.JournalEntry;
import com.financial.accounting.exception.ResourceNotFoundException;
import com.financial.accounting.repository.JournalRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JournalService {
    private final JournalRepository repository;
    public JournalService(JournalRepository repository) { this.repository = repository; }
    public List<JournalEntry> findAll() { return repository.findAll(); }
    public JournalEntry save(JournalEntry item) { return repository.save(item); }
    public JournalEntry findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Journal entry", id));
    }
    public JournalEntry update(Long id, JournalEntry item) {
        JournalEntry existing = findById(id);
        existing.setDate(item.getDate());
        existing.setDescription(item.getDescription());
        existing.setDebit(item.getDebit());
        existing.setCredit(item.getCredit());
        return repository.save(existing);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
