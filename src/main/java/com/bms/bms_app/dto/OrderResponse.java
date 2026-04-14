package com.bms.bms_app.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private String status;
    private Double totalAmount;
    private List<OrderItemResponse> items;

}
