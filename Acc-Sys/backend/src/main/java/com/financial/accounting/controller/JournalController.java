package com.financial.accounting.controller;

import com.financial.accounting.entity.JournalEntry;
import com.financial.accounting.service.JournalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/journals")
public class JournalController {
    private final JournalService service;
    public JournalController(JournalService service) { this.service = service; }
    @GetMapping public List<JournalEntry> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public JournalEntry findById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public JournalEntry create(@Valid @RequestBody JournalEntry item) { return service.save(item); }
    @PutMapping("/{id}") public JournalEntry update(@PathVariable Long id, @Valid @RequestBody JournalEntry item) { return service.update(id, item); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
