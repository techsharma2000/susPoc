package com.sushil.poc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaturityDateException.class)
    @ResponseBody
    public ResponseEntity<String> handleMaturityDateException(MaturityDateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maturity date error: " + ex.getMessage());
    }

    @ExceptionHandler(LowerVersionException.class)
    @ResponseBody
    public ResponseEntity<String> handleLowerVersionException(LowerVersionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Version error: " + ex.getMessage());
    }

    // Optionally handle other exceptions
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + ex.getMessage());
    }
}
