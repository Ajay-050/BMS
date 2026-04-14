package com.bms.bms_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bms.bms_app.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}