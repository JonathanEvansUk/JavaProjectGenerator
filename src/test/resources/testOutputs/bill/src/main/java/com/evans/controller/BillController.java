package com.evans.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

import com.evans.repository.Bill;
import com.evans.service.BillService;
import java.lang.Long;

@CrossOrigin
@RestController
@RequestMapping("/bill")
public class BillController {

  private final BillService billService;

  public BillController(BillService billService) {
    this.billService = billService;
  }

  @GetMapping
  public List<Bill> findAll() {
    return billService.findAll();
  }

  @GetMapping("/{id}")
  public Optional<Bill> findById(@PathVariable Long id) {
    return billService.findById(id);
  }

  @PostMapping
  public Bill create(@RequestBody Bill bill) {
    return billService.save(bill);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Bill> delete(@PathVariable Long id) {
    Optional<Bill> bill = billService.delete(id);

    if (bill.isPresent()) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}