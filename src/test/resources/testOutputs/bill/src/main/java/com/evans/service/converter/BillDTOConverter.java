package com.evans.service.converter;

import org.springframework.stereotype.Service;

import com.evans.repository.Bill;
import com.evans.openapi.model.BillDTO;

@Service
public class BillDTOConverter {

  public Bill convert(BillDTO billDTO) {
    Bill bill = new Bill();

    bill.setId(billDTO.getId());
    bill.setAmount(billDTO.getAmount());
    bill.setDateReceived(billDTO.getDateReceived());
    bill.setPaid(billDTO.getPaid());
    bill.setDatePaid(billDTO.getDatePaid());

    return bill;
  }

  public BillDTO convert(Bill bill) {
    BillDTO billDTO = new BillDTO();

    billDTO.setId(bill.getId());
    billDTO.setAmount(bill.getAmount());
    billDTO.setDateReceived(bill.getDateReceived());
    billDTO.setPaid(bill.getPaid());
    billDTO.setDatePaid(bill.getDatePaid());

    return billDTO;
  }
}
