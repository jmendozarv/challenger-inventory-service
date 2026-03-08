package com.challenge.inventory.service;

import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for inventory operations.
 * All operations return reactive types (Mono/Flux).
 */
public interface InventoryService {
  /**
   * Creates or updates inventory for a product.
   * @param request the inventory request
   * @return mono with created/updated inventory response
   */
  Mono<InventoryResponse> createInventory(InventoryRequest request);

  /**
   * Retrieves all inventory records.
   * @return flux of inventory responses
   */
  Flux<InventoryResponse> getAllInventory();

  /**
   * Retrieves inventory for a specific product.
   * @param productId the product ID
   * @return mono with inventory response
   */
  Mono<InventoryResponse> getInventoryByProductId(Long productId);
}
