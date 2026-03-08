package com.challenge.inventory.service.impl;

import com.challenge.inventory.entity.InventoryEntity;
import com.challenge.inventory.exception.InventoryNotFoundException;
import com.challenge.inventory.mapper.InventoryMapper;
import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import com.challenge.inventory.repository.InventoryRepository;
import com.challenge.inventory.service.InventoryService;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementation of inventory service with reactive operations.
 * Uses bounded elastic scheduler for blocking JPA operations.
 */
@Service
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private static final String MSG_PRODUCT_ID_REQUIRED = "Product id is required";
  private static final String MSG_STOCK_REQUIRED = "Stock must be greater than or equal to 0";
  private static final String MSG_REQUEST_NULL = "Inventory request cannot be null";
  private static final String MSG_UNEXPECTED_INVENTORY_FLOW_ERROR =
      "Unexpected error while processing inventory flow";

  private final InventoryRepository inventoryRepository;
  private final InventoryMapper inventoryMapper;

  @Override
  public Mono<InventoryResponse> createInventory(InventoryRequest request) {
    Consumer<InventoryRequest> validateRequest = req -> Optional.ofNullable(req)
        .ifPresentOrElse(inventory -> {
          Optional.ofNullable(inventory.getProductId())
              .orElseThrow(() -> new IllegalArgumentException(MSG_PRODUCT_ID_REQUIRED));

          Optional.ofNullable(inventory.getStock())
              .filter(stock -> stock >= 0)
              .orElseThrow(() -> new IllegalArgumentException(MSG_STOCK_REQUIRED));
        }, () -> {
          throw new IllegalArgumentException(MSG_REQUEST_NULL);
        });
    return Mono.fromCallable(() -> {
          validateRequest.accept(request);
          InventoryEntity entityToSave = inventoryRepository.findByProductId(request.getProductId())
              .map(existing -> {
                existing.setStock(request.getStock());
                return existing;
              })
              .orElseGet(() -> inventoryMapper.toEntity(request));
          return inventoryRepository.save(entityToSave);
        })
        .map(inventoryMapper::toResponse)
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(this::mapUnexpectedError);
  }

  @Override
  public Flux<InventoryResponse> getAllInventory() {
    Predicate<InventoryEntity> validEntity = entity -> Optional.ofNullable(entity)
        .map(inventory -> inventory.getProductId() != null && inventory.getStock() != null)
        .orElse(false);

    return Mono.fromSupplier(inventoryRepository::findAll)
        .map(inventories -> inventories.stream()
            .filter(validEntity)
            .map(inventoryMapper::toResponse)
            .toList())
        .flatMapMany(Flux::fromIterable)
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(this::mapUnexpectedError);
  }

  @Override
  public Mono<InventoryResponse> getInventoryByProductId(Long productId) {
    Supplier<InventoryNotFoundException> notFoundSupplier =
        () -> new InventoryNotFoundException(productId);

    return Mono.fromSupplier(() -> inventoryRepository.findByProductId(productId)
            .orElseThrow(notFoundSupplier))
        .map(inventoryMapper::toResponse)
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(this::mapUnexpectedError);
  }

  private Throwable mapUnexpectedError(Throwable error) {
    if (error instanceof IllegalArgumentException || error instanceof InventoryNotFoundException) {
      return error;
    }
    return new IllegalStateException(MSG_UNEXPECTED_INVENTORY_FLOW_ERROR, error);
  }
}
