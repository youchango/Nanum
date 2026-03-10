package com.nanum.admin.manager.controller;

import com.nanum.admin.manager.dto.ManagerDTO;
import com.nanum.admin.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminManager", description = "AdminManager API")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminManagerController {

    private final ManagerService managerService;

    @Operation(summary = "신규 관리자 계정 생성", description = "요청받은 CreateRequest DTO를 기반으로 데이터베이스의 manager 테이블에 새로운 관리자 정보를 등록합니다.")
    @PostMapping("/managers")
    public ResponseEntity<Void> createManager(@RequestBody ManagerDTO.CreateRequest request) {
        managerService.createManager(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 관리자 목록 조회", description = "요청받은 SearchDTO의 페이징 및 검색 조건을 기반으로 데이터베이스의 manager 테이블에서 관리자 목록을 조회하고, 결과를 반환합니다.")
    @org.springframework.web.bind.annotation.GetMapping("/managers")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<java.util.Map<String, Object>>> getManagers(
            @org.springframework.web.bind.annotation.ModelAttribute com.nanum.global.common.dto.SearchDTO searchDTO) {
        org.springframework.data.domain.Page<ManagerDTO.ManagerInfo> managerPage = managerService
                .getManagers(searchDTO);

        java.util.Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("managerList", managerPage.getContent());
        responseData.put("totalCount", managerPage.getTotalElements());

        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(responseData));
    }

    @Operation(summary = "단일 관리자 상세 조회", description = "요청받은 managerCode를 기반으로 데이터베이스의 manager 테이블을 조회하고, 해당 관리자의 상세 정보를 반환합니다.")
    @org.springframework.web.bind.annotation.GetMapping("/managers/{managerCode}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<ManagerDTO.ManagerInfo>> getManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode) {
        return ResponseEntity
                .ok(com.nanum.global.common.dto.ApiResponse.success(managerService.getManager(managerCode)));
    }

    @Operation(summary = "관리자 상세 정보 수정", description = "요청받은 managerCode와 수정 데이터를 바탕으로 데이터베이스의 manager 테이블 내 관리자 정보를 변경하고, 결과를 반환합니다.")
    @org.springframework.web.bind.annotation.PutMapping("/managers/{managerCode}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> updateManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode,
            @RequestBody ManagerDTO.CreateRequest request) {
        managerService.updateManager(managerCode, request);
        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(null));
    }

    @Operation(summary = "대기 관리자 가입 승인", description = "요청받은 managerCode를 기반으로 데이터베이스의 manager 테이블을 조회하고, 미승인 상태의 관리자를 승인 완료 처리합니다.")
    @PostMapping("/managers/{managerCode}/approve")
    public ResponseEntity<Void> approveManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode) {
        managerService.approveManager(managerCode);
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자를 승인 취소 또는 삭제(비활성화) 처리합니다.
     */
    @Operation(summary = "관리자 계정 논리 삭제", description = "요청받은 managerCode를 기반으로 데이터베이스의 manager 테이블을 조회하고, 해당 관리자를 비활성화(삭제) 처리합니다.")
    @org.springframework.web.bind.annotation.DeleteMapping("/managers/{managerCode}")
    public ResponseEntity<com.nanum.global.common.dto.ApiResponse<Void>> deleteManager(
            @org.springframework.web.bind.annotation.PathVariable String managerCode) {
        managerService.deleteManager(managerCode);
        return ResponseEntity.ok(com.nanum.global.common.dto.ApiResponse.success(null));
    }

}
