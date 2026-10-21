package com.financial.accounting.controller;

import com.financial.accounting.entity.Purchase;
import com.financial.accounting.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    private final PurchaseService service;
    public PurchaseController(PurchaseService service) { this.service = service; }
    @GetMapping public List<Purchase> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public Purchase findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public Purchase create(@Valid @RequestBody Purchase item) { return service.save(item); }
    @PutMapping("/{id}") public Purchase update(@PathVariable Long id, @Valid @RequestBody Purchase item) { return service.update(id, item); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
