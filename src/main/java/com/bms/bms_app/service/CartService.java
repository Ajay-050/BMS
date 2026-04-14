package com.bms.bms_app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.bms.bms_app.dto.CartCreateRequest;
import com.bms.bms_app.dto.CartItemDto;
import com.bms.bms_app.dto.CartItemRequest;
import com.bms.bms_app.dto.CartResponse;
import com.bms.bms_app.dto.OrderItemRequest;
import com.bms.bms_app.dto.OrderRequest;
import com.bms.bms_app.dto.OrderResponse;
import com.bms.bms_app.exception.ResourceNotFoundException;
import com.bms.bms_app.model.Cart;
import com.bms.bms_app.model.CartItem;
import com.bms.bms_app.model.Order;
import com.bms.bms_app.model.OrderItem;
import com.bms.bms_app.model.Product;
import com.bms.bms_app.model.User;
import com.bms.bms_app.repository.CartRepository;
import com.bms.bms_app.repository.OrderItemRepository;
import com.bms.bms_app.repository.OrderRepository;
import com.bms.bms_app.repository.ProductRepository;
import com.bms.bms_app.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    public CartService(CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderService orderService) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }

    public CartResponse createCart(CartCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Check if cart already exists for this user
        Optional<Cart> existingCart = cartRepository.findByUserId(user.getId());
        if (existingCart.isPresent()) {
            throw new IllegalStateException("Cart already exists for user with id: " + request.getUserId());
        }

        // Generate cart ID as "cart-{userId}"
        String cartId = "cart-" + user.getId();

        Cart cart = Cart.builder()
                .id(cartId)
                .user(user)
                .build();

        cart = cartRepository.save(cart);
        return mapToResponse(cart);
    }

    public CartResponse addCartItemToCart(String cartId, CartItemRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (Double.compare(request.getPrice(), product.getPrice()) != 0) {
            throw new IllegalArgumentException("Cart item price must match product price (" + product.getPrice() + ")");
        }

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();

        // Initialize items list if null
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // Add item to cart's items list
        cart.getItems().add(cartItem);

        // Save cart (cascade will save the CartItem)
        cart = cartRepository.save(cart);

        return mapToResponse(cart);
    }

    public void removeCartItemFromCart(String cartId, Long cartItemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));


        // Remove item from cart's items list
        if (cart.getItems() != null) {
            cart.getItems().remove(cartItem);
        }

        // Save cart (cascade will delete the CartItem)
        cartRepository.save(cart);
    }

    public OrderResponse checkoutCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty, nothing to checkout");
        }

        // Calculate total amount from cart items
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Create order request without itemIds (we'll create OrderItems separately)
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(cart.getUser().getId())
                .status("PENDING")
                .totalAmount(totalAmount)
                .build();

        // Create the order
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        // Get the created order
        Order order = orderRepository.findByIdWithItems(orderResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found after creation"));

        // Convert CartItems to OrderItems and associate with the order
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            order.getItems().add(orderItem);
        }

        // Save the order with items
        orderRepository.save(order);

        // Clear cart items and save cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderService.mapToResponse(order);
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemDto> itemResponses = cart.getItems() != null
                ? cart.getItems().stream().map(this::mapCartItemToDto).collect(Collectors.toList())
                : List.of();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .build();
    }

    private CartItemDto mapCartItemToDto(CartItem cartItem) {
        return CartItemDto.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }

}
