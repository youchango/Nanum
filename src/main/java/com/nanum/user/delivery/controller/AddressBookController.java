package com.nanum.user.delivery.controller;

import com.nanum.domain.delivery.dto.AddressBookDTO;
import com.nanum.global.common.dto.ApiResponse;
import com.nanum.user.delivery.service.AddressBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "AddressBook", description = "배송지 관리 API")
@RestController
@RequestMapping("/api/v1/address-book")
@RequiredArgsConstructor
public class AddressBookController {

    private final AddressBookService addressBookService;

    @Operation(summary = "배송지 목록 조회", description = "현재 로그인한 사용자의 배송지 목록을 기본배송지 우선, 최신순으로 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressBookDTO.Response>>> getMyAddresses(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("배송지 목록 조회 성공",
                addressBookService.getMyAddresses(principal.getName())));
    }

    @Operation(summary = "배송지 등록", description = "새로운 배송지를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createAddress(
            @Valid @RequestBody AddressBookDTO.CreateRequest request,
            Principal principal) {
        Long addressId = addressBookService.createAddress(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("배송지가 등록되었습니다.", addressId));
    }

    @Operation(summary = "배송지 수정", description = "기존 배송지 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateAddress(
            @PathVariable("id") Long id,
            @Valid @RequestBody AddressBookDTO.UpdateRequest request,
            Principal principal) {
        addressBookService.updateAddress(principal.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("배송지가 수정되었습니다.", null));
    }

    @Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable("id") Long id,
            Principal principal) {
        addressBookService.deleteAddress(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("배송지가 삭제되었습니다.", null));
    }

    @Operation(summary = "기본 배송지 설정", description = "선택한 배송지를 기본 배송지로 설정합니다.")
    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @PathVariable("id") Long id,
            Principal principal) {
        addressBookService.setDefault(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("기본 배송지로 설정되었습니다.", null));
    }
}
