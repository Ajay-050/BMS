package com.bms.bms_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.CartCreateRequest;
import com.bms.bms_app.dto.CartItemRequest;
import com.bms.bms_app.dto.CartResponse;
import com.bms.bms_app.dto.OrderResponse;
import com.bms.bms_app.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> createCart(@Valid @RequestBody CartCreateRequest request) {
        CartResponse responseData = cartService.createCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Cart created", responseData));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<CartResponse>> addCartItemToCart(
            @PathVariable String cartId,
            @Valid @RequestBody CartItemRequest request) {
        CartResponse responseData = cartService.addCartItemToCart(cartId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Item added to cart", responseData));
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItemFromCart(
            @PathVariable String cartId,
            @PathVariable Long itemId) {
        cartService.removeCartItemFromCart(cartId, itemId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Item removed from cart", null));
    }

    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkoutCart(@PathVariable String cartId) {
        OrderResponse orderResponse = cartService.checkoutCart(cartId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Checkout successful", orderResponse));
    }

}
