package com.nanum.user.point.controller;

import com.nanum.domain.point.dto.PointDto;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.point.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Point", description = "Point API")
@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 잔액 조회", description = "현재 로그인한 사용자의 총 포인트 잔액(적립 - 사용)을 조회합니다.")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Integer>> getBalance(Principal principal) {
        int balance = pointService.getBalance(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("포인트 잔액 조회 성공", balance));
    }

    @Operation(summary = "포인트 내역 조회", description = "현재 로그인한 사용자의 포인트 거래 내역을 페이징하여 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<PointDto>>> getHistory(
            Principal principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PointDto> history = pointService.getHistory(principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("포인트 내역 조회 성공", history));
    }
}
