package com.challenge.inventory.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.challenge.inventory.entity.InventoryEntity;
import com.challenge.inventory.exception.InventoryNotFoundException;
import com.challenge.inventory.mapper.InventoryMapper;
import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import com.challenge.inventory.repository.InventoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

  @Mock
  private InventoryRepository inventoryRepository;

  @Mock
  private InventoryMapper inventoryMapper;

  private InventoryServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new InventoryServiceImpl(inventoryRepository, inventoryMapper);
  }

  @Test
  void createInventoryShouldFailWhenProductIdIsMissing() {
    InventoryRequest request = new InventoryRequest();
    request.setStock(10);

    StepVerifier.create(service.createInventory(request))
        .expectErrorSatisfies(error -> {
          assertTrue(error instanceof IllegalArgumentException);
          assertEquals("Product id is required", error.getMessage());
        })
        .verify();
  }

  @Test
  void getAllInventoryShouldFilterInvalidEntitiesAndMapValidOnes() {
    InventoryEntity valid = InventoryEntity.builder().id(1L).productId(100L).stock(5).build();
    InventoryEntity invalid = InventoryEntity.builder().id(2L).productId(null).stock(3).build();

    InventoryResponse mapped = new InventoryResponse().id(1L).productId(100L).stock(5);

    when(inventoryRepository.findAll()).thenReturn(List.of(valid, invalid));
    when(inventoryMapper.toResponse(valid)).thenReturn(mapped);

    StepVerifier.create(service.getAllInventory())
        .expectNext(mapped)
        .verifyComplete();
  }

  @Test
  void getInventoryByProductIdShouldPreserveNotFoundError() {
    long productId = 99L;
    when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

    StepVerifier.create(service.getInventoryByProductId(productId))
        .expectError(InventoryNotFoundException.class)
        .verify();
  }

  @Test
  void getInventoryByProductIdShouldMapUnexpectedError() {
    long productId = 33L;
    when(inventoryRepository.findByProductId(productId)).thenThrow(new RuntimeException("db down"));

    StepVerifier.create(service.getInventoryByProductId(productId))
        .expectErrorSatisfies(error -> {
          assertTrue(error instanceof IllegalStateException);
          assertEquals("Unexpected error while processing inventory flow", error.getMessage());
        })
        .verify();
  }

  @Test
  void createInventoryShouldReturnMappedResponseWhenProductDoesNotExist() {
    InventoryRequest request = new InventoryRequest(200L, 8);
    InventoryEntity entity = InventoryEntity.builder().productId(200L).stock(8).build();
    InventoryEntity saved = InventoryEntity.builder().id(1L).productId(200L).stock(8).build();
    InventoryResponse response = new InventoryResponse().id(1L).productId(200L).stock(8);

    when(inventoryRepository.findByProductId(200L)).thenReturn(Optional.empty());
    when(inventoryMapper.toEntity(request)).thenReturn(entity);
    when(inventoryRepository.save(entity)).thenReturn(saved);
    when(inventoryMapper.toResponse(saved)).thenReturn(response);

    StepVerifier.create(service.createInventory(request))
        .expectNext(response)
        .verifyComplete();
  }

  @Test
  void createInventoryShouldUpdateStockWhenProductAlreadyExists() {
    InventoryRequest request = new InventoryRequest(200L, 15);
    InventoryEntity existing = InventoryEntity.builder().id(9L).productId(200L).stock(3).build();
    InventoryEntity saved = InventoryEntity.builder().id(9L).productId(200L).stock(15).build();
    InventoryResponse response = new InventoryResponse().id(9L).productId(200L).stock(15);

    when(inventoryRepository.findByProductId(200L)).thenReturn(Optional.of(existing));
    when(inventoryRepository.save(existing)).thenReturn(saved);
    when(inventoryMapper.toResponse(saved)).thenReturn(response);

    StepVerifier.create(service.createInventory(request))
        .expectNext(response)
        .verifyComplete();

    assertEquals(15, existing.getStock());
    verify(inventoryMapper, org.mockito.Mockito.never()).toEntity(request);
  }
}
