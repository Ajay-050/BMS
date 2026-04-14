package com.bms.bms_app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.OrderItemRequest;
import com.bms.bms_app.dto.OrderItemResponse;
import com.bms.bms_app.service.OrderItemService;



@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getAllOrderItems() {
        List<OrderItemResponse> orderItemResponses = orderItemService.getAllOrderItems();
        ApiResponse<List<OrderItemResponse>> response = new ApiResponse<>(true, "All order items retrieved successfully", orderItemResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> getOrderItemById(@PathVariable Long id) {
        logger.info("[OrderItemController] getOrderItemById() - Input Parameter: id={}", id);
        
        OrderItemResponse orderItemResponse = orderItemService.getOrderItemById(id);
        ApiResponse<OrderItemResponse> response = new ApiResponse<>(true, "Order item retrieved successfully", orderItemResponse);
        return ResponseEntity.ok(response);
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Order item deleted successfully", null);
        return ResponseEntity.ok(response);
    }

}
