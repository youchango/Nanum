package com.nanum.admin.content.controller;

import com.nanum.admin.content.service.AdminContentService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.global.security.CustomUserDetails;
import com.nanum.user.content.model.Content;
import com.nanum.user.content.model.ContentDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Admin Content", description = "관리자 컨텐츠 관리 API")
@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @Operation(summary = "컨텐츠 목록 조회", description = "컨텐츠 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(@ModelAttribute SearchDTO searchDTO) {
        // 기본 정렬 및 페이징 설정
        if (searchDTO.getPage() < 1)
            searchDTO.setPage(1);
        if (searchDTO.getRecordSize() < 10)
            searchDTO.setRecordSize(10);

        // 검색 조건에 따른 데이터 조회
        List<Content> contentList = adminContentService.findContentList(searchDTO);
        int totalCount = adminContentService.findContentCount(searchDTO);
        searchDTO.setPagination(totalCount);

        // Entity -> DTO 변환
        List<ContentDTO> contentDTOList = contentList.stream()
                .map(Content::toDTO)
                .collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("contentList", contentDTOList);
        responseData.put("totalCount", totalCount);
        responseData.put("pagination", searchDTO);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @Operation(summary = "컨텐츠 상세 조회", description = "컨텐츠 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentDTO>> detail(@PathVariable int id) {
        Content content = adminContentService.findContentById(id);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(content.toDTO()));
    }

    @Operation(summary = "컨텐츠 등록", description = "새로운 컨텐츠를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody ContentDTO contentDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberCode = userDetails.getMember().getMemberCode();
        adminContentService.registerContent(contentDTO, memberCode);

        return ResponseEntity.ok(ApiResponse.success("컨텐츠가 등록되었습니다.", null));
    }

    @Operation(summary = "컨텐츠 수정", description = "컨텐츠 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable int id,
            @RequestBody ContentDTO contentDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        contentDTO.setContentId(id);
        String memberCode = userDetails.getMember().getMemberCode();
        adminContentService.updateContent(contentDTO, memberCode);

        return ResponseEntity.ok(ApiResponse.success("컨텐츠가 수정되었습니다.", null));
    }

    @Operation(summary = "컨텐츠 삭제", description = "컨텐츠를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable int id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String deletedBy = userDetails.getMember().getMemberCode();
        adminContentService.deleteContent(id, deletedBy);

        return ResponseEntity.ok(ApiResponse.success("컨텐츠가 삭제되었습니다.", null));
    }
}
