package com.evans.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

import com.evans.controller.dto.BillDTO;
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
  public List<BillDTO> findAll() {
    return billService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<BillDTO> findById(@PathVariable Long id) {
    Optional<BillDTO> bill = billService.findById(id);

    return ResponseEntity.of(bill);
  }

  @PostMapping
  public BillDTO create(@RequestBody BillDTO bill) {
    return billService.save(bill);
  }

  @PutMapping("/{id}")
  public BillDTO update(@PathVariable Long id, @RequestBody BillDTO bill) {
    return billService.update(id, bill);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BillDTO> delete(@PathVariable Long id) {
    Optional<BillDTO> bill = billService.delete(id);

    if (bill.isPresent()) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
