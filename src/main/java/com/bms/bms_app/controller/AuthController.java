package com.bms.bms_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.LoginRequest;
import com.bms.bms_app.dto.LoginResponse;
import com.bms.bms_app.dto.RegisterRequest;
import com.bms.bms_app.dto.UserResponse;
import com.bms.bms_app.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {

        UserResponse response = userService.register(registerRequest);

        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(true, "User registered successfully", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response = userService.login(loginRequest);

        if ("ADMIN".equalsIgnoreCase(response.getRole())) {
            ApiResponse<LoginResponse> errorResponse = new ApiResponse<>(false, "Admins should use /admin/login", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(true, "Login successful", response);
        return ResponseEntity.ok(apiResponse);
    }
}
