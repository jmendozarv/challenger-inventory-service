package com.challenge.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application for Inventory Service.
 */
@SpringBootApplication
public class InventoryServiceApplication {

	/**
	 * Application entry point.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
