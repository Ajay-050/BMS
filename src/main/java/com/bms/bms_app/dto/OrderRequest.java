package com.bms.bms_app.dto;

import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
public class OrderRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    private String status;  // Made optional for cart creation
    
    private Double totalAmount;  // Total amount for the order
    
    private List<Long> itemIds;  // Item IDs to associate with the order
}


