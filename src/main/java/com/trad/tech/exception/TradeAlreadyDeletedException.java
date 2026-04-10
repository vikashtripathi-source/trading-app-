package com.trad.tech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE)
public class TradeAlreadyDeletedException extends RuntimeException {
    
    public TradeAlreadyDeletedException(String message) {
        super(message);
    }
    
    public TradeAlreadyDeletedException(String id, boolean useIdMessage) {
        super("Trade with ID '" + id + "' has already been deleted and cannot be found");
    }
}
