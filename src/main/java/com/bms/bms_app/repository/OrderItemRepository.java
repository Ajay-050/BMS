package com.bms.bms_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bms.bms_app.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
}