package com.bms.bms_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.LoginRequest;
import com.bms.bms_app.dto.LoginResponse;
import com.bms.bms_app.dto.UserRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // CREATE ADMIN
    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(@Valid @RequestBody UserRequest userRequest) {
        log.debug("Create admin request received for email={}", userRequest.getEmail());
        UserResponse userResponse = userService.createUser(userRequest);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "Admin created successfully", userResponse);
        log.info("Admin created successfully with email={}", userRequest.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // ADMIN LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.debug("Admin login attempt for email={}", loginRequest.getEmail());
        LoginResponse loginResponse = userService.login(loginRequest);

        if (!"ADMIN".equalsIgnoreCase(loginResponse.getRole())) {
            ApiResponse<LoginResponse> errorResponse = new ApiResponse<>(false, "Only admin login allowed", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        ApiResponse<LoginResponse> response = new ApiResponse<>(true, "Admin login successful", loginResponse);
        return ResponseEntity.ok(response);
    }
}