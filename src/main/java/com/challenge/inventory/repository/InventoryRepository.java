package com.challenge.inventory.repository;

import com.challenge.inventory.entity.InventoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
  Optional<InventoryEntity> findByProductId(Long productId);
}
