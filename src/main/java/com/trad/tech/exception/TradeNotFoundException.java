package com.trad.tech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TradeNotFoundException extends RuntimeException {

  public TradeNotFoundException(String message) {
    super(message);
  }

  public TradeNotFoundException(String id, Throwable cause) {
    super("Trade not found with ID: " + id, cause);
  }
}
