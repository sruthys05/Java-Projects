package com.financial.accounting.service;

import com.financial.accounting.entity.Receipt;
import com.financial.accounting.exception.ResourceNotFoundException;
import com.financial.accounting.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReceiptService {
    private final ReceiptRepository repository;
    public ReceiptService(ReceiptRepository repository) { this.repository = repository; }
    public List<Receipt> findAll() { return repository.findAll(); }
    public Receipt save(Receipt item) { return repository.save(item); }
    public Receipt findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Receipt", id));
    }
    public Receipt update(Long id, Receipt item) {
        Receipt existing = findById(id);
        existing.setDate(item.getDate());
        existing.setFromName(item.getFromName());
        existing.setDescription(item.getDescription());
        existing.setAmount(item.getAmount());
        return repository.save(existing);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
