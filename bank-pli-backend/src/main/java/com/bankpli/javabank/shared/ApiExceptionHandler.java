package com.bankpli.javabank.shared;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<?> business(BusinessException ex) {
        return ResponseEntity.status(ex.status()).body(Map.of("timestamp", Instant.now(), "error", ex.getMessage()));
    }
    @ExceptionHandler(BindException.class)
    ResponseEntity<?> validation(BindException ex) {
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "error",
            ex.getAllErrors().stream().findFirst().map(e -> e.getDefaultMessage()).orElse("Validation failed")));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> unexpected(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("timestamp", Instant.now(), "error", "Request failed"));
    }
}
