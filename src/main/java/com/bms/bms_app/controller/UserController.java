package com.bms.bms_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.UserRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/users")

public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }   

    // CREATE
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User created successfully", userResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Iterable<UserResponse>>> getAllUsers() {
        log.debug("Get all users requested");
        Iterable<UserResponse> userResponses = userService.getAllUsers();
        ApiResponse<Iterable<UserResponse>> response = new ApiResponse<>(true, "All the Users retrieved successfully", userResponses);
        log.info("Returning {} users", ((userResponses instanceof java.util.Collection) ? ((java.util.Collection<?>) userResponses).size() : "(unknown)"));
        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.debug("Get user by id requested id={}", id);
        UserResponse userResponse = userService.getUserById(id);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User retrieved successfully", userResponse);
        log.info("Returning user id={}", id);
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")       
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id,@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.updateUser(id, userRequest);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User updated successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.debug("Delete request received for user id={}", id);
        userService.deleteUser(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "User deleted successfully", null);
        log.info("User deleted successfully id={}", id);
        return ResponseEntity.ok(response);
    }
}
