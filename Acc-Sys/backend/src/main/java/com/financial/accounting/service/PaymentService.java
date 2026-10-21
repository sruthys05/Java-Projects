package com.financial.accounting.service;

import com.financial.accounting.entity.Payment;
import com.financial.accounting.exception.ResourceNotFoundException;
import com.financial.accounting.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    public PaymentService(PaymentRepository repository) { this.repository = repository; }
    public List<Payment> findAll() { return repository.findAll(); }
    public Payment save(Payment item) { return repository.save(item); }
    public Payment findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }
    public Payment update(Long id, Payment item) {
        Payment existing = findById(id);
        existing.setDate(item.getDate());
        existing.setToName(item.getToName());
        existing.setDescription(item.getDescription());
        existing.setAmount(item.getAmount());
        return repository.save(existing);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
