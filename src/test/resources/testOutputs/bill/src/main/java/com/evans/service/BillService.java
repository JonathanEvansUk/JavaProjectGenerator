package com.evans.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.evans.repository.Bill;
import com.evans.repository.BillRepository;
import java.lang.Long;

@Service
public class BillService {

  private final BillRepository billRepository;

  public BillService(BillRepository billRepository) {
    this.billRepository = billRepository;
  }

  public List<Bill> findAll() {
    return billRepository.findAll();
  }

  public Optional<Bill> findById(Long id) {
    return billRepository.findById(id);
  }

  public Bill save(Bill bill) {
    return billRepository.save(bill);
  }

  public Optional<Bill> delete(Long id) {
    Optional<Bill> bill = billRepository.findById(id);

    if (bill.isPresent()) {
      billRepository.delete(bill.get());
    }

    return bill;
  }
}
