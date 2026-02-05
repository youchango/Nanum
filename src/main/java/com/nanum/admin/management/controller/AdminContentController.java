package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminContentService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.content.dto.ContentDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public ApiResponse<List<ContentDTO.Response>> getContents() {
        return ApiResponse.success(adminContentService.getContents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회")
    public ApiResponse<ContentDTO.Response> getContent(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.getContent(id));
    }

    @PostMapping
    @Operation(summary = "게시글 등록")
    public ApiResponse<Long> createContent(@RequestBody ContentDTO.Request request) {
        return ApiResponse.success(adminContentService.createContent(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정")
    public ApiResponse<Void> updateContent(@PathVariable Long id, @RequestBody ContentDTO.Request request) {
        adminContentService.updateContent(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제")
    public ApiResponse<Void> deleteContent(@PathVariable Long id) {
        adminContentService.deleteContent(id);
        return ApiResponse.success(null);
    }
}
