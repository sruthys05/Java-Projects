package com.financial.accounting.service;

import com.financial.accounting.entity.Purchase;
import com.financial.accounting.exception.ResourceNotFoundException;
import com.financial.accounting.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PurchaseService {
    private final PurchaseRepository repository;
    public PurchaseService(PurchaseRepository repository) { this.repository = repository; }
    public List<Purchase> findAll() { return repository.findAll(); }
    public Purchase save(Purchase item) { return repository.save(item); }
    public Purchase findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase", id));
    }
    public Purchase update(Long id, Purchase item) {
        Purchase existing = findById(id);
        existing.setDate(item.getDate());
        existing.setSupplier(item.getSupplier());
        existing.setDescription(item.getDescription());
        existing.setAmount(item.getAmount());
        return repository.save(existing);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
