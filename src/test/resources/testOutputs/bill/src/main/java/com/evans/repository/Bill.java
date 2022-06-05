package com.evans.repository;

import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.Long;
import java.lang.Double;
import java.time.LocalDate;
import java.lang.Boolean;
import java.time.Instant;

@Entity
public class Bill {

  @Id
  //@GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double amount;

  private LocalDate dateReceived;

  private Boolean paid;

  private Instant datePaid;

  private PaymentType paymentType;

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

  public PaymentType getPaymentType() {
    return paymentType;
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

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public enum PaymentType {
    CREDIT("Credit"),
    DEBIT("Debit");

    private final String value;

    @JsonValue
    private PaymentType(String value) {
      this.value = value;
    }

    @JsonCreator
    public static PaymentType fromValue(String value) {
      return Arrays.stream(PaymentType.values())
        .filter(entry -> entry.value)
        .findFirst()
        .get();
    }
  }
}