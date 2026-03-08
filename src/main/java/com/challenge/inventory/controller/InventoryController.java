package com.challenge.inventory.controller;

import com.challenge.inventory.service.InventoryService;
import com.challenge.inventory.api.InventoryApi;
import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class InventoryController implements InventoryApi {

  private final InventoryService inventoryService;

  @Override
  public Mono<ResponseEntity<InventoryResponse>> createInventory(
      Mono<InventoryRequest> inventoryRequest, ServerWebExchange exchange) {
    return inventoryRequest
        .flatMap(inventoryService::createInventory)
        .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
  }

  @Override
  public Mono<ResponseEntity<Flux<InventoryResponse>>> getAllInventory(
      ServerWebExchange exchange) {
    return Mono.just(ResponseEntity.ok(inventoryService.getAllInventory()));
  }

  @Override
  public Mono<ResponseEntity<InventoryResponse>> getInventoryByProductId(Long productId,
                                                                         ServerWebExchange exchange) {
    return inventoryService.getInventoryByProductId(productId)
        .map(ResponseEntity::ok);
  }
}
