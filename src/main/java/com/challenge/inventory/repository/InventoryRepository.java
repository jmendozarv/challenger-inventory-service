package com.challenge.inventory.repository;

import com.challenge.inventory.entity.InventoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for inventory entities.
 */
@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
  /**
   * Finds inventory by product ID.
   * @param productId the product ID
   * @return optional inventory entity
   */
  Optional<InventoryEntity> findByProductId(Long productId);
}
