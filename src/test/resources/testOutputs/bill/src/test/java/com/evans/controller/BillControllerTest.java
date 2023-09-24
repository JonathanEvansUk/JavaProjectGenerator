package com.evans.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

import com.evans.service.BillService;
import com.evans.openapi.model.BillDTO;

@ExtendWith(MockitoExtension.class)
public class BillControllerTest {

  private BillController billController;

  @Mock
  private BillService billService;

  @BeforeEach
  public void setUp() {
    this.billController = new BillController(billService);
  }

  @Test
  public void getAllBill() {
    when(billService.findAll()).thenReturn(List.of());

    ResponseEntity<List<BillDTO>> response = billController.getAllBill();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

  @Test
  public void getBillById() {
    long id = 1L;
    when(billService.findById(id)).thenReturn(Optional.empty());

    ResponseEntity<BillDTO> byId = billController.getBillById(id);

    assertEquals(HttpStatus.NOT_FOUND, byId.getStatusCode());
    assertFalse(byId.hasBody());
  }

  @Test
  public void createBill() {
    BillDTO billDTO = new BillDTO();

    when(billService.save(billDTO)).thenReturn(billDTO);

    ResponseEntity<BillDTO> response = billController.createBill(billDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(billDTO, response.getBody());
  }

  @Test
  public void updateBillById() {
    long id = 1L;
    BillDTO billDTO = new BillDTO();

    when(billService.update(id, billDTO)).thenReturn(billDTO);

    ResponseEntity<BillDTO> response = billController.updateBillById(id, billDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(billDTO, response.getBody());
  }

  @Test
  public void deleteBillById_notFound() {
    long id = 1L;

    when(billService.delete(id)).thenReturn(Optional.empty());

    ResponseEntity<BillDTO> response = billController.deleteBillById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void deleteBillById() {
    long id = 1L;

    BillDTO billDTO = new BillDTO();

    when(billService.delete(id)).thenReturn(Optional.of(billDTO));

    ResponseEntity<BillDTO> response = billController.deleteBillById(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertFalse(response.hasBody());
  }
}
