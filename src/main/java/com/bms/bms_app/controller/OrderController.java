package com.bms.bms_app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.OrderRequest;
import com.bms.bms_app.dto.OrderResponse;
import com.bms.bms_app.dto.OrderItemResponse;
import com.bms.bms_app.service.OrderService;
import com.bms.bms_app.service.OrderItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Order created successfully", orderResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        ApiResponse<List<OrderResponse>> response = new ApiResponse<>(true, "All orders retrieved successfully", orderResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Order retrieved successfully", orderResponse);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Order deleted successfully", null);
        return ResponseEntity.ok(response);
    }

}
