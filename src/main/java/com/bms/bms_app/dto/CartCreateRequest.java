package com.bms.bms_app.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CartCreateRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

}
