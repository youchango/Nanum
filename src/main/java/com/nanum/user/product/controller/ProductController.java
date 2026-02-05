package com.nanum.user.product.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.user.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductDTO.Response>> getProducts(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(productService.getProductList(categoryId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO.Response> getProduct(@PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }
}
