package com.bms.bms_app.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class CartItemDto {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Double price;

}