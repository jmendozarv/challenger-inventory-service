package com.challenge.inventory.mapper;

import com.challenge.inventory.entity.InventoryEntity;
import com.challenge.inventory.model.InventoryRequest;
import com.challenge.inventory.model.InventoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

  InventoryEntity toEntity(InventoryRequest request);

  InventoryResponse toResponse(InventoryEntity entity);
}