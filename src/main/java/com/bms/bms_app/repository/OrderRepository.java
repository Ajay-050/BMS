package com.bms.bms_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.bms.bms_app.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserIdAndStatus(Long userId, String status);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(Long id);
}