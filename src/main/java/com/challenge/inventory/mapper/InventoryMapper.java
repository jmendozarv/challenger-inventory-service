package com.challenge.inventory.mapper;

import com.challenge.inventory.entity.InventoryEntity;
import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for inventory entity and model transformations.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

  /**
   * Converts inventory request to entity.
   * @param request the inventory request
   * @return the inventory entity
   */
  InventoryEntity toEntity(InventoryRequest request);

  /**
   * Converts inventory entity to response.
   * @param entity the inventory entity
   * @return the inventory response
   */
  InventoryResponse toResponse(InventoryEntity entity);
}