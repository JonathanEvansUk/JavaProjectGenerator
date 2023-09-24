package com.evans.controller;

import com.evans.openapi.api.BillApi;
import com.evans.openapi.model.BillDTO;
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

import com.evans.openapi.model.BillDTO;
import com.evans.service.BillService;
import java.lang.Long;

@CrossOrigin
@RestController
@RequestMapping("/bill")
public class BillController implements BillApi {

  private final BillService billService;

  public BillController(BillService billService) {
    this.billService = billService;
  }

  @GetMapping
  public ResponseEntity<List<BillDTO>> getAllBill() {
    return ResponseEntity.ok(billService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
    Optional<BillDTO> bill = billService.findById(id);

    return ResponseEntity.of(bill);
  }

  @PostMapping
  public ResponseEntity<BillDTO> createBill(@RequestBody BillDTO bill) {
    return ResponseEntity.status(201).body(billService.save(bill));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BillDTO> updateBillById(@PathVariable Long id, @RequestBody BillDTO bill) {
    return ResponseEntity.ok(billService.update(id, bill));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BillDTO> deleteBillById(@PathVariable Long id) {
    Optional<BillDTO> bill = billService.delete(id);

    if (bill.isPresent()) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
