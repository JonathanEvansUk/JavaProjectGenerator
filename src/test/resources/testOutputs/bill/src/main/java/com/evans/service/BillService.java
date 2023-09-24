package com.evans.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.evans.repository.Bill;
import com.evans.openapi.model.BillDTO;
import com.evans.service.converter.BillDTOConverter;
import com.evans.repository.BillRepository;
import java.lang.Long;

@Service
public class BillService {

  private final BillRepository billRepository;
  private final BillDTOConverter billDTOConverter;

  public BillService(BillRepository billRepository, BillDTOConverter billDTOConverter
) {
    this.billRepository = billRepository;
    this.billDTOConverter = billDTOConverter;
  }

  public List<BillDTO> findAll() {
    return billRepository.findAll()
        .stream()
        .map(this::convert)
        .toList();
  }

  public Optional<BillDTO> findById(Long id) {
    return billRepository.findById(id)
        .map(this::convert);
  }

  public BillDTO save(BillDTO billDTO) {

    Bill bill = convert(billDTO);


    Bill savedBill = billRepository.save(bill);

    return convert(savedBill);
  }

  public BillDTO update(Long id, BillDTO billDTO) {
    Optional<Bill> currentBill = billRepository.findById(id);

    Bill bill = convert(billDTO);


    Bill savedBill = billRepository.save(bill);

    return convert(savedBill);
  }

  public Optional<BillDTO> delete(Long id) {
    Optional<Bill> bill = billRepository.findById(id);

    if (bill.isPresent()) {
      billRepository.delete(bill.get());
    }

    return bill.map(this::convert);
  }

  private Bill convert(BillDTO billDTO) {
    return billDTOConverter.convert(billDTO);
  }

  private BillDTO convert(Bill bill) {
    return billDTOConverter.convert(bill);
  }
}
