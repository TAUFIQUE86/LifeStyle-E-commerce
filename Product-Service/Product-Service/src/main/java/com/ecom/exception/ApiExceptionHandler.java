package com.ecom.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.ecom.service.ProductAccessDeniedException;
import com.ecom.service.ProductNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        log.warn("Rejected product request with {} validation errors", exception.getErrorCount());
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(Map.of(
                "message", "Validation failed",
                "errors", fields
        ));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ProductNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found"));
    }

    @ExceptionHandler(ProductAccessDeniedException.class)
    ResponseEntity<Map<String, String>> handleForbidden(ProductAccessDeniedException exception) {
        log.warn("Rejected a product ownership operation");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not own this product"));
    }
}
