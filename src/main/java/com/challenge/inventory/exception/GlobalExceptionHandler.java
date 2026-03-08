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

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String MSG_UNEXPECTED_INTERNAL_ERROR = "Unexpected internal error";

  @ExceptionHandler(InventoryNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInventoryNotFound(InventoryNotFoundException ex) {
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(IllegalArgumentException ex) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedState(IllegalStateException ex) {
    LOGGER.error("Controlled internal error while processing request", ex);
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, MSG_UNEXPECTED_INTERNAL_ERROR);
  }

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

