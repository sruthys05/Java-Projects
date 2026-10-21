package com.financial.accounting.controller;

import com.financial.accounting.entity.Payment;
import com.financial.accounting.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }
    @GetMapping public List<Payment> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public Payment findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public Payment create(@Valid @RequestBody Payment item) { return service.save(item); }
    @PutMapping("/{id}") public Payment update(@PathVariable Long id, @Valid @RequestBody Payment item) { return service.update(id, item); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
