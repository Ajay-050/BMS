package com.bms.bms_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bms.bms_app.model.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUserId(Long userId);

}
