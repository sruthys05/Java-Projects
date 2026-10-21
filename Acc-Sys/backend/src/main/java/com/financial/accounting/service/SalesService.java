package com.financial.accounting.service;

import com.financial.accounting.entity.Sales;
import com.financial.accounting.exception.ResourceNotFoundException;
import com.financial.accounting.repository.SalesRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SalesService {
    private final SalesRepository repository;
    public SalesService(SalesRepository repository) { this.repository = repository; }
    public List<Sales> findAll() { return repository.findAll(); }
    public Sales save(Sales item) { return repository.save(item); }
    public Sales findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale", id));
    }
    public Sales update(Long id, Sales item) {
        Sales existing = findById(id);
        existing.setDate(item.getDate());
        existing.setCustomer(item.getCustomer());
        existing.setDescription(item.getDescription());
        existing.setAmount(item.getAmount());
        return repository.save(existing);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
