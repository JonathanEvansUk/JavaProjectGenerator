package com.evans.controller;

import com.evans.openapi.model.BillDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mysql")
public class BillControllerIT {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void getAllBill() {
    webTestClient.get().uri("/bill")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void getBillById() {
    webTestClient.get().uri("/bill/{id}", 99L)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void create() {
    BillDTO billDTO = new BillDTO();
    billDTO.setAmount(12.34);
    billDTO.setDateReceived(LocalDate.parse("2023-01-03"));
    billDTO.setPaid(true);
    billDTO.setDatePaid(Instant.parse("2023-01-03T10:15:30.00Z"));

    webTestClient
        .post().uri("/bill")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(billDTO)
        .exchange()
        .expectStatus().isCreated();
  }

  @Test
  public void update() {
    //add first post
    BillDTO billDTO = new BillDTO();
    billDTO.setAmount(12.34);
    billDTO.setDateReceived(LocalDate.parse("2023-01-03"));
    billDTO.setPaid(true);
    billDTO.setDatePaid(Instant.parse("2023-01-03T10:15:30.00Z"));

    webTestClient
        .post().uri("/bill")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(billDTO)
        .exchange()
        .expectStatus().isCreated();

    BillDTO updatedBillDTO = new BillDTO();
    updatedBillDTO.setId(1L);
    updatedBillDTO.setAmount(12.34);
    updatedBillDTO.setDateReceived(LocalDate.parse("2023-01-03"));
    updatedBillDTO.setPaid(true);
    updatedBillDTO.setDatePaid(Instant.parse("2023-01-03T10:15:30.00Z"));

    webTestClient.put().uri("/bill/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updatedBillDTO)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void delete() {
    //add first post
    BillDTO billDTO = new BillDTO();
    billDTO.setAmount(12.34);
    billDTO.setDateReceived(LocalDate.parse("2023-01-03"));
    billDTO.setPaid(true);
    billDTO.setDatePaid(Instant.parse("2023-01-03T10:15:30.00Z"));

    BillDTO savedBillDTO = webTestClient
        .post().uri("/bill")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(billDTO)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(BillDTO.class)
        .returnResult()
        .getResponseBody();

    webTestClient.delete().uri("/bill/{id}", savedBillDTO.getId())
        .exchange()
        .expectStatus().isNoContent();

    webTestClient.get().uri("/bill/{id}", savedBillDTO.getId())
        .exchange()
        .expectStatus().isNotFound();
  }
}
