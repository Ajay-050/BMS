package com.bms.bms_app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bms.bms_app.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ApiResponse<Object> response = new ApiResponse<>(false, "Validation failed", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Handle Data Integrity Violations (e.g., duplicate email)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(Exception ex) {

        String message = ex.getCause().getMessage();

        String error;

        if (message.contains("email")) {
            error = "Email already exists";
        } else if (message.contains("phone")) {
            error = "Phone number already exists";
        } else {
            error = "Duplicate or invalid data";
        }

        ApiResponse<Object> response = new ApiResponse<>(false, error, null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Handle Invalid Credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    // Handle Generic Exceptions
    @ExceptionHandler(Exception.class)  
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(false, "An unexpected error occurred", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
