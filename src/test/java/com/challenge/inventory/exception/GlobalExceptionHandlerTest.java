package com.challenge.inventory.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.challenge.inventory.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void shouldMapInventoryNotFoundTo404() {
    Mono<ResponseEntity<ErrorResponse>> result =
        handler.handleInventoryNotFound(new InventoryNotFoundException(10L));

    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals(404, response.getBody().getStatus());
        })
        .verifyComplete();
  }

  @Test
  void shouldMapIllegalArgumentTo400() {
    Mono<ResponseEntity<ErrorResponse>> result =
        handler.handleBadRequest(new IllegalArgumentException("invalid"));

    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals(400, response.getBody().getStatus());
        })
        .verifyComplete();
  }

  @Test
  void shouldMapUnexpectedTo500() {
    Mono<ResponseEntity<ErrorResponse>> result =
        handler.handleUnexpected(new RuntimeException("boom"));

    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals(500, response.getBody().getStatus());
        })
        .verifyComplete();
  }
}

