package com.challenge.inventory.service;

import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryService {
  Mono<InventoryResponse> createInventory(InventoryRequest request);

  Flux<InventoryResponse> getAllInventory();

  Mono<InventoryResponse> getInventoryByProductId(Long productId);
}
