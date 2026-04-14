package com.bms.bms_app.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class OrderItemResponse {

    private Long id;
    private Long userId;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double price;
    private Double subtotal;

}
