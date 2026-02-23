package com.bms.bms_app.exception;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();

        response.put("status", Response.SC_BAD_REQUEST);
        response.put("message", "Validation Failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("status", Response.SC_NOT_FOUND);
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle Data Integrity Violations (e.g., duplicate email)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(Exception ex) {

        String message = ex.getCause().getMessage();

        String error;

        if (message.contains("email")) {
            error = "Email already exists";
        } else if (message.contains("phone")) {
            error = "Phone number already exists";
        } else {
            error = "Duplicate or invalid data";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", error);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle Generic Exceptions
    @ExceptionHandler(Exception.class)  
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("status", Response.SC_INTERNAL_SERVER_ERROR);
        response.put("message", "An unexpected error occurred");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
