package com.financial.accounting.controller;

import com.financial.accounting.entity.Receipt;
import com.financial.accounting.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptService service;
    public ReceiptController(ReceiptService service) { this.service = service; }
    @GetMapping public List<Receipt> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public Receipt findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public Receipt create(@Valid @RequestBody Receipt item) { return service.save(item); }
    @PutMapping("/{id}") public Receipt update(@PathVariable Long id, @Valid @RequestBody Receipt item) { return service.update(id, item); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
