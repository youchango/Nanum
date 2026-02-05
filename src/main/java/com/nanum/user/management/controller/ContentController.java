package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.content.dto.ContentDTO;
import com.nanum.domain.content.model.ContentType;
import com.nanum.user.management.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public ApiResponse<List<ContentDTO.Response>> getContents(@RequestParam ContentType type) {
        return ApiResponse.success(contentService.getContents(type));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회")
    public ApiResponse<ContentDTO.Response> getContent(@PathVariable Long id) {
        return ApiResponse.success(contentService.getContent(id));
    }
}
