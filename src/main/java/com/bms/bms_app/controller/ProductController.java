package com.bms.bms_app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.ProductRequest;
import com.bms.bms_app.dto.ProductResponse;
import com.bms.bms_app.service.ProductService;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can create products
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProduct(productRequest);
        ApiResponse<ProductResponse> response = new ApiResponse<>(true, "Product created successfully", productResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')") // only admin can view products
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {    
        List<ProductResponse> productResponses = productService.getAllProducts();
        ApiResponse<List<ProductResponse>> response = new ApiResponse<>(true, "All the Products retrieved successfully", productResponses);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        ApiResponse<ProductResponse> response = new ApiResponse<>(true, "Product retrieved successfully", productResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can update products
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.updateProduct(id, productRequest);
        ApiResponse<ProductResponse> response = new ApiResponse<>(true, "Product updated successfully", productResponse);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can partially update products
    public ResponseEntity<ApiResponse<ProductResponse>> partialUpdateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.partialUpdateProduct(id, productRequest);
        ApiResponse<ProductResponse> response = new ApiResponse<>(true, "Product partially updated successfully", productResponse);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can delete products
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) { 
        productService.deleteProduct(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Product deleted successfully", null);
        return ResponseEntity.ok(response);
    }

}
