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
import com.evans.controller.dto.BillDTO;

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
  public void findAll() {
    when(billService.findAll()).thenReturn(List.of());

    List<BillDTO> all = billController.findAll();

    assertTrue(all.isEmpty());
  }

  @Test
  public void findById() {
    long id = 1L;
    when(billService.findById(id)).thenReturn(Optional.empty());

    ResponseEntity<BillDTO> byId = billController.findById(id);

    assertEquals(HttpStatus.NOT_FOUND, byId.getStatusCode());
    assertFalse(byId.hasBody());
  }

  @Test
  public void create() {
    BillDTO billDTO = new BillDTO();

    when(billService.save(billDTO)).thenReturn(billDTO);

    BillDTO created = billController.create(billDTO);

    assertEquals(billDTO, created);
  }

  @Test
  public void update() {
    long id = 1L;
    BillDTO billDTO = new BillDTO();

    when(billService.update(id, billDTO)).thenReturn(billDTO);

    BillDTO updated = billController.update(id, billDTO);

    assertEquals(billDTO, updated);
  }

  @Test
  public void delete_notFound() {
    long id = 1L;

    when(billService.delete(id)).thenReturn(Optional.empty());

    ResponseEntity<BillDTO> response = billController.delete(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void delete() {
    long id = 1L;

    BillDTO billDTO = new BillDTO();

    when(billService.delete(id)).thenReturn(Optional.of(billDTO));

    ResponseEntity<BillDTO> response = billController.delete(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertFalse(response.hasBody());
  }
}
