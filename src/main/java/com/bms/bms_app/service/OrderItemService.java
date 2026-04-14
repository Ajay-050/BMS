package com.bms.bms_app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bms.bms_app.dto.OrderItemRequest;
import com.bms.bms_app.dto.OrderItemResponse;
import com.bms.bms_app.model.Order;
import com.bms.bms_app.model.OrderItem;
import com.bms.bms_app.model.Product;
import com.bms.bms_app.repository.OrderItemRepository;
import com.bms.bms_app.repository.OrderRepository;
import com.bms.bms_app.repository.ProductRepository;
import com.bms.bms_app.exception.ResourceNotFoundException;

@Service
public class OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderItemResponse mapToResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subtotal(orderItem.getQuantity() * orderItem.getPrice())
                .build();
    }

    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderItemResponse getOrderItemById(Long id) {
        logger.info("[OrderItemService] getOrderItemById() - Input Parameter: id={}", id);
        
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[OrderItemService] getOrderItemById() - OrderItem not found in database with id={}", id);
                    return new ResourceNotFoundException("OrderItem not found with id: " + id);
                });
        
        logger.debug("[OrderItemService] getOrderItemById() - OrderItem retrieved from DB: id={}, quantity={}, price={}", 
                orderItem.getId(), orderItem.getQuantity(), orderItem.getPrice());

        OrderItemResponse response = mapToResponse(orderItem);

        return response;
        
    }

    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + id));
        orderItemRepository.delete(orderItem);
    }

}
