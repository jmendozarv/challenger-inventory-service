package com.challenge.inventory.exception;

/**
 * Exception thrown when inventory for a product is not found.
 */
public class InventoryNotFoundException extends RuntimeException {

  /**
   * Creates a new inventory not found exception.
   * @param productId the product ID that was not found
   */
  public InventoryNotFoundException(Long productId) {
    super("Inventory not found for product id: " + productId);
  }
}
