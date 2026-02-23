package com.bms.bms_app.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
