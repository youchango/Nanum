package com.nanum.admin.management.controller;

import com.nanum.admin.management.service.AdminContentService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.domain.content.dto.ContentDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import com.nanum.domain.content.dto.ContentSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminContent", description = "AdminContent API")
@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "데이터베이스의 content 테이블에서 등록된 게시글 목록을 조회합니다. SearchDTO를 통해 사이트 코드, 컨텐츠 타입, 키워드 검색 및 페이징이 가능합니다.")
    public ApiResponse<Page<ContentDTO.Response>> getContents(
            @ModelAttribute ContentSearchDTO searchDTO,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success(adminContentService.getContents(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 식별자로 하여 특정 게시글의 상세 내용을 조회하고 반환합니다.")
    public ApiResponse<ContentDTO.Response> getContent(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.getContent(id));
    }

    @PostMapping
    @Operation(summary = "게시글 등록", description = "게시글 정보(ContentDTO.Request)를 전달받아 데이터베이스에 새로운 게시글을 등록합니다.")
    public ApiResponse<Long> createContent(@RequestBody ContentDTO.Request request) {
        return ApiResponse.success(adminContentService.createContent(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "게시글 ID와 수정 요청 데이터를 전달받아 기존 게시글 정보를 업데이트합니다.")
    public ApiResponse<Void> updateContent(@PathVariable Long id, @RequestBody ContentDTO.Request request) {
        adminContentService.updateContent(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 식별자로 하여 해당 게시글을 데이터베이스에서 논리적으로 삭제 처리합니다.")
    public ApiResponse<Void> deleteContent(@PathVariable Long id) {
        adminContentService.deleteContent(id);
        return ApiResponse.success(null);
    }
}
