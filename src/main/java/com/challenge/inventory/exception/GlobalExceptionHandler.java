package com.challenge.inventory.exception;

import com.challenge.inventory.model.ErrorResponse;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for reactive inventory endpoints.
 * Maps exceptions to standardized error responses with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String MSG_UNEXPECTED_INTERNAL_ERROR = "Unexpected internal error";

  /**
   * Handles inventory not found exceptions.
   * @param ex the inventory not found exception
   * @return 404 error response
   */
  @ExceptionHandler(InventoryNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInventoryNotFound(InventoryNotFoundException ex) {
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Handles illegal argument exceptions from validation failures.
   * @param ex the illegal argument exception
   * @return 400 error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(IllegalArgumentException ex) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Handles controlled internal state errors.
   * @param ex the illegal state exception
   * @return 500 error response
   */
  @ExceptionHandler(IllegalStateException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedState(IllegalStateException ex) {
    LOGGER.error("Controlled internal error while processing request", ex);
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, MSG_UNEXPECTED_INTERNAL_ERROR);
  }

  /**
   * Handles unexpected unhandled exceptions.
   * @param ex the throwable
   * @return 500 error response
   */
  @ExceptionHandler(Throwable.class)
  public Mono<ResponseEntity<ErrorResponse>> handleUnexpected(Throwable ex) {
    LOGGER.error("Unhandled error while processing request", ex);
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, MSG_UNEXPECTED_INTERNAL_ERROR);
  }

  private Mono<ResponseEntity<ErrorResponse>> buildError(HttpStatus status, String message) {
    ErrorResponse response = new ErrorResponse()
        .message(message)
        .status(status.value())
        .timestamp(OffsetDateTime.now());
    return Mono.just(ResponseEntity.status(status).body(response));
  }
}

