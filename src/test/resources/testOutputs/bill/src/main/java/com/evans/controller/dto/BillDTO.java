package com.evans.controller.dto;

import java.lang.Long;
import java.lang.Double;
import java.time.LocalDate;
import java.lang.Boolean;
import java.time.Instant;

public class BillDTO {

    private Long id;
    private Double amount;
    private LocalDate dateReceived;
    private Boolean paid;
    private Instant datePaid;

    public Long getId() {
      return id;
    }

    public Double getAmount() {
      return amount;
    }

    public LocalDate getDateReceived() {
      return dateReceived;
    }

    public Boolean getPaid() {
      return paid;
    }

    public Instant getDatePaid() {
      return datePaid;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public void setAmount(Double amount) {
      this.amount = amount;
    }

    public void setDateReceived(LocalDate dateReceived) {
      this.dateReceived = dateReceived;
    }

    public void setPaid(Boolean paid) {
      this.paid = paid;
    }

    public void setDatePaid(Instant datePaid) {
      this.datePaid = datePaid;
    }
}
