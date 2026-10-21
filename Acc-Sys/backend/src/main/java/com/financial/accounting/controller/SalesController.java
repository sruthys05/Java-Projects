package com.financial.accounting.controller;

import com.financial.accounting.entity.Sales;
import com.financial.accounting.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    private final SalesService service;
    public SalesController(SalesService service) { this.service = service; }
    @GetMapping public List<Sales> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public Sales findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public Sales create(@Valid @RequestBody Sales item) { return service.save(item); }
    @PutMapping("/{id}") public Sales update(@PathVariable Long id, @Valid @RequestBody Sales item) { return service.update(id, item); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
