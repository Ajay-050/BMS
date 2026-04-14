package com.bms.bms_app.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
public class CartResponse {

    private String id;
    private Long userId;
    private List<CartItemDto> items;

}
