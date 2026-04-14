package com.bms.bms_app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bms.bms_app.dto.OrderRequest;
import com.bms.bms_app.dto.OrderResponse;
import com.bms.bms_app.dto.OrderItemResponse;
import com.bms.bms_app.model.Order;
import com.bms.bms_app.model.User;
import com.bms.bms_app.model.OrderItem;
import com.bms.bms_app.repository.OrderRepository;
import com.bms.bms_app.repository.UserRepository;
import com.bms.bms_app.repository.OrderItemRepository;
import com.bms.bms_app.exception.ResourceNotFoundException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemService orderItemService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
                       OrderItemRepository orderItemRepository, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemService = orderItemService;
    }

    public OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems() != null 
            ? order.getItems().stream().map(orderItemService::mapToResponse).toList()
            : List.of();
            
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .build();
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + orderRequest.getUserId()));

        // Business rule: User can only have one active CART at a time
        String status = orderRequest.getStatus() != null ? orderRequest.getStatus() : "CART";
        if ("CART".equals(status)) {
            // Check if user already has an active cart
            List<Order> existingCarts = orderRepository.findByUserIdAndStatus(user.getId(), "CART");
            if (!existingCarts.isEmpty()) {
                throw new IllegalStateException("User already has an active cart. Cannot create multiple carts.");
            }
        }

        Double totalAmount = orderRequest.getTotalAmount() != null ? orderRequest.getTotalAmount() : 0.0;

        Order order = Order.builder()
                .user(user)
                .status(status)
                .totalAmount(totalAmount)
                .build();

        // Initialize order's items list if null
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }

        // Add existing OrderItems to this order if itemIds are provided
        if (orderRequest.getItemIds() != null && !orderRequest.getItemIds().isEmpty()) {
            for (Long itemId : orderRequest.getItemIds()) {
                OrderItem orderItem = orderItemRepository.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + itemId));
                orderItem.setOrder(order);
                order.getItems().add(orderItem);
            }
        }

        // Save order (cascade will save all items)
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(orderRepository.findByIdWithItems(savedOrder.getId()).get());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    public List<Order> findByUserIdAndStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }
    
}
