package com.nanum.admin.product.controller;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.domain.shipment.dto.ShipmentDTO;
import com.nanum.domain.shipment.service.ShipmentService;
import com.nanum.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 출고지/입고지 관리 컨트롤러 (관리자용)
 */
@RestController
@RequestMapping("/api/v1/admin/shipments")
@RequiredArgsConstructor
public class AdminShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentDTO.Response>>> getShipments(
            @AuthenticationPrincipal CustomManagerDetails managerDetails) {
        Manager manager = managerDetails.getManager();
        List<ShipmentDTO.Response> response = shipmentService.getShipments(manager.getManagerCode(), manager.getMbType().name());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> getShipment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.getShipment(id)));
    }

    @GetMapping("/default/{type}")
    public ResponseEntity<ApiResponse<ShipmentDTO.Response>> getDefaultShipment(
            @AuthenticationPrincipal CustomManagerDetails managerDetails,
            @PathVariable String type) {
        Manager manager = managerDetails.getManager();
        ShipmentDTO.Response response = shipmentService.getDefaultShipment(manager.getManagerCode(), type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createShipment(
            @AuthenticationPrincipal CustomManagerDetails managerDetails,
            @RequestBody ShipmentDTO.Request request) {
        Manager manager = managerDetails.getManager();
        Long id = shipmentService.createShipment(manager.getManagerCode(), request);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateShipment(
            @AuthenticationPrincipal CustomManagerDetails managerDetails,
            @PathVariable Long id,
            @RequestBody ShipmentDTO.Request request) {
        Manager manager = managerDetails.getManager();
        shipmentService.updateShipment(id, manager.getManagerCode(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
