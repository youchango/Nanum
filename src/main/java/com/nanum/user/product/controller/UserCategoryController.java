package com.nanum.user.product.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.product.dto.UserCategoryDTO;
import com.nanum.user.product.service.UserCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "User Category", description = "사용자용 카테고리 API")
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    @GetMapping
    @Operation(summary = "카테고리 트리 조회", description = "사용 가능한 카테고리 트리를 조회합니다.")
    public ResponseEntity<ApiResponse<List<UserCategoryDTO>>> getCategoryTree() {
        List<UserCategoryDTO> categories = userCategoryService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
