package com.nanum.user.management.controller;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.support.ResponseSupport;
import com.nanum.domain.content.dto.ContentDTO;
import com.nanum.domain.content.model.ContentType;
import com.nanum.user.management.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Content", description = "Content API")
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentController implements ResponseSupport {

    private final ContentService contentService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "게시글 유형(ContentType: NOTICE, FAQ 등)에 따라 등록된 게시글 목록을 조회하여 반환합니다. 키워드로 검색이 가능합니다.")
    public ResponseEntity<ApiResponse<List<ContentDTO.Response>>> getContents(
            @RequestParam ContentType type,
            @RequestParam(name = "site_cd") String siteCd,
            @RequestParam(required = false) String keyword) {
        return success(contentService.getContents(type, siteCd, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 식별자로 하여 특정 게시글의 상세 내용을 조회하고 반환합니다.")
    public ResponseEntity<ApiResponse<ContentDTO.Response>> getContent(
            @PathVariable Long id,
            @RequestParam(name = "site_cd") String siteCd) {
        return success(contentService.getContent(id, siteCd));
    }
}
