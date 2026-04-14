package com.bms.bms_app.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class ProductResponse {
    
    private Long id;
    private String name;
    private double price;
    private String description;
    private int stock;

}
