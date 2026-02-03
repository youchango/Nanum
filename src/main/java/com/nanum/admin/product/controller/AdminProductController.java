package com.nanum.admin.product.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.product.dto.ProductDTO;
import com.nanum.user.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Long> createProduct(@RequestBody ProductDTO.Request request) {
        Long productId = productService.createProduct(request);
        return ApiResponse.success(productId);
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateProduct(@PathVariable Long id, @RequestBody ProductDTO.Request request) {
        productService.updateProduct(id, request);
        return ApiResponse.success("Product Updated");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Product Deleted");
    }
}
