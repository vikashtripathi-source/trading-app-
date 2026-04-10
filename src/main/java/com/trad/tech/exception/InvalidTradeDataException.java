package com.trad.tech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidTradeDataException extends RuntimeException {
    
    public InvalidTradeDataException(String message) {
        super(message);
    }
    
    public InvalidTradeDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
