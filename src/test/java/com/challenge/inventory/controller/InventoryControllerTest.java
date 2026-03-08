package com.challenge.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.challenge.inventory.model.InventoryResponse;
import com.challenge.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = InventoryController.class)
class InventoryControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private InventoryService inventoryService;

  @Test
  void createInventoryShouldReturn201Created() {
    InventoryResponse created = new InventoryResponse()
        .id(1L)
        .productId(1L)
        .stock(20);

    when(inventoryService.createInventory(any())).thenReturn(Mono.just(created));

    webTestClient.post()
        .uri("/inventory")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"productId\":1,\"stock\":20}")
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.id").isEqualTo(1)
        .jsonPath("$.productId").isEqualTo(1)
        .jsonPath("$.stock").isEqualTo(20);
  }
}
