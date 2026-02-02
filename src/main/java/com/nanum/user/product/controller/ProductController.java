package com.nanum.user.product.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.product.dto.ProductDTO;
import com.nanum.user.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Long> createProduct(@RequestBody ProductDTO.Request request) {
        Long productId = productService.createProduct(request);
        return ApiResponse.success(productId);
    }

    @GetMapping
    public ApiResponse<List<ProductDTO.Response>> getProducts(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(productService.getProductList(categoryId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO.Response> getProduct(@PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }
}
