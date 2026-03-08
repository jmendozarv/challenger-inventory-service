package com.challenge.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.challenge.inventory.exception.InventoryNotFoundException;
import com.challenge.inventory.model.InventoryResponse;
import com.challenge.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
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

  @Test
  void getAllInventoryShouldReturn200() {
    InventoryResponse first = new InventoryResponse().id(1L).productId(1L).stock(20);
    InventoryResponse second = new InventoryResponse().id(2L).productId(2L).stock(15);

    when(inventoryService.getAllInventory()).thenReturn(Flux.just(first, second));

    webTestClient.get()
        .uri("/inventory")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].id").isEqualTo(1)
        .jsonPath("$[1].productId").isEqualTo(2)
        .jsonPath("$[1].stock").isEqualTo(15);
  }

  @Test
  void getInventoryByProductIdShouldReturn404WhenNotFound() {
    when(inventoryService.getInventoryByProductId(99L))
        .thenReturn(Mono.error(new InventoryNotFoundException(99L)));

    webTestClient.get()
        .uri("/inventory/99")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  void createInventoryShouldReturn400WhenServiceRejectsRequest() {
    when(inventoryService.createInventory(any()))
        .thenReturn(Mono.error(new IllegalArgumentException("Product id is required")));

    webTestClient.post()
        .uri("/inventory")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"productId\":1,\"stock\":20}")
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }
}
