package com.evans.controller;

import com.evans.controller.dto.BillDTO;

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
  public void findAll() {
    webTestClient.get().uri("/bill")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void findById() {
    webTestClient.get().uri("/bill/{id}", 99L)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void create() {
    BillDTO billDTO = new BillDTO();
    billDTO.setAmount(12.34);
    billDTO.setDateReceived(LocalDate.parse("25/12/2022"));
    billDTO.setPaid(true);
    billDTO.setDatePaid(Instant.parse("25/12/2022T00:00:00"));
    billDTO.setPaymentType(Credit);

    webTestClient
        .post().uri("/bill")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(billDTO)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void update() {
    //add first post
    BillDTO billDTO = new BillDTO();
    billDTO.setAmount(12.34);
    billDTO.setDateReceived(LocalDate.parse("25/12/2022"));
    billDTO.setPaid(true);
    billDTO.setDatePaid(Instant.parse("25/12/2022T00:00:00"));
    billDTO.setPaymentType(Credit);

    webTestClient
        .post().uri("/bill")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(billDTO)
        .exchange()
        .expectStatus().isOk();

    BillDTO updatedBillDTO = new BillDTO();
    updatedBillDTO.setId(1L);
    updatedBillDTO.setAmount(12.34);
    updatedBillDTO.setDateReceived(LocalDate.parse("25/12/2022"));
    updatedBillDTO.setPaid(true);
    updatedBillDTO.setDatePaid(Instant.parse("25/12/2022T00:00:00"));
    updatedBillDTO.setPaymentType(Credit);

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
    billDTO.setDateReceived(LocalDate.parse("25/12/2022"));
    billDTO.setPaid(true);
    billDTO.setDatePaid(Instant.parse("25/12/2022T00:00:00"));
    billDTO.setPaymentType(Credit);

    BillDTO savedBillDTO = webTestClient
        .post().uri("/bill")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(billDTO)
        .exchange()
        .expectStatus().isOk()
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
