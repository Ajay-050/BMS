package com.bms.bms_app.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Name is required")  
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    private String description;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

}
