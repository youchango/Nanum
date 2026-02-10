package com.nanum.admin.code.controller;

import com.nanum.admin.code.service.CodeService;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.global.security.CustomUserDetails;
import com.nanum.domain.code.model.Code;
import com.nanum.domain.code.dto.CodeDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자용 코드 관리 REST 컨트롤러
 */
@Slf4j
@Tag(name = "Admin Code", description = "관리자 코드 관리 API")
@RestController
@RequestMapping("/api/v1/admin/codes")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @Operation(summary = "코드 목록 조회", description = "검색 조건에 맞는 코드 목록(계층형)을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> codeList(@ModelAttribute SearchDTO searchDTO) {
        log.info("코드 목록 페이지 요청 - searchDTO: {}", searchDTO);

        List<CodeDTO> codeList = codeService.getHierarchicalCodeList(searchDTO);
        int totalCount = codeService.getCodeCount(searchDTO);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("codeList", codeList);
        responseData.put("totalCount", totalCount);

        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @Operation(summary = "상위 코드 목록 조회", description = "코드 등록/수정 시 사용할 상위 코드 목록을 조회합니다.")
    @GetMapping("/upper")
    public ResponseEntity<ApiResponse<List<Code>>> getUpperCodes() {
        List<Code> upperCodes = codeService.getUpperCodes();
        return ResponseEntity.ok(ApiResponse.success(upperCodes));
    }

    @Operation(summary = "코드 상세 조회", description = "특정 코드를 조회합니다.")
    @GetMapping("/{codeId}")
    public ResponseEntity<ApiResponse<Code>> getCodeDetail(@PathVariable int codeId) {
        Code code = codeService.getCodeDetail(codeId);
        if (code == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(code));
    }

    @Operation(summary = "코드 등록", description = "새로운 코드를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> codeCreate(@Valid @RequestBody CodeDTO codeDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("코드 등록 요청 - codeDTO: {}", codeDTO);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed: " + bindingResult.getAllErrors()));
        }

        try {
            String createdBy = userDetails.getMember().getMemberCode();
            codeService.createCode(codeDTO, createdBy);
            return ResponseEntity.ok(ApiResponse.success("코드가 등록되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "코드 수정", description = "코드를 수정합니다.")
    @PutMapping("/{codeId}")
    public ResponseEntity<ApiResponse<Object>> codeUpdate(@PathVariable int codeId,
            @Valid @RequestBody CodeDTO codeDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("코드 수정 요청 - codeId: {}, codeDTO: {}", codeId, codeDTO);

        codeDTO.setCodeId(codeId);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed: " + bindingResult.getAllErrors()));
        }

        try {
            codeService.updateCode(codeDTO);
            return ResponseEntity.ok(ApiResponse.success("코드가 수정되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "코드 삭제", description = "코드를 삭제합니다.")
    @DeleteMapping("/{codeId}")
    public ResponseEntity<ApiResponse<Object>> codeDelete(@PathVariable int codeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("코드 삭제 요청 - codeId: {}", codeId);

        try {
            String deletedBy = userDetails.getMember().getMemberCode();
            codeService.deleteCode(codeId, deletedBy);
            return ResponseEntity.ok(ApiResponse.success("코드가 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "코드 타입별 조회", description = "특정 코드 타입에 해당하는 코드 목록을 조회합니다.")
    @GetMapping("/api/types/{codeType}")
    public ResponseEntity<ApiResponse<List<Code>>> getCodesByType(@PathVariable String codeType) {
        log.info("코드 타입별 조회 API - codeType: {}", codeType);

        List<Code> codes = codeService.getCodesByType(codeType);
        return ResponseEntity.ok(ApiResponse.success(codes));
    }
}
