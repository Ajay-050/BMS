package com.bms.bms_app.dto;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")   
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    private String role; 

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}$", message = "Phone must be 10 digits")
    private String phone;


}
