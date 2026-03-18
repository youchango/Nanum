package com.nanum.domain.shipment.service;

import com.nanum.domain.shipment.dto.ShipmentDTO;
import com.nanum.domain.shipment.model.Shipment;
import com.nanum.domain.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public List<ShipmentDTO.Response> getShipments(String managerCode, String mbType) {
        List<Shipment> shipments;
        if ("MASTER".equals(mbType)) {
            shipments = shipmentRepository.findByDeleteYn("N");
        } else {
            shipments = shipmentRepository.findByManagerCodeAndDeleteYn(managerCode, "N");
        }
        return shipments.stream()
                .map(ShipmentDTO.Response::from)
                .collect(Collectors.toList());
    }

    public ShipmentDTO.Response getShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다. ID: " + id));
        return ShipmentDTO.Response.from(shipment);
    }

    public ShipmentDTO.Response getDefaultShipment(String managerCode, String shipmentType) {
        List<Shipment> shipments = shipmentRepository.findByManagerCodeAndDeleteYn(managerCode, "N");
        return shipments.stream()
                .filter(s -> shipmentType.equals(s.getShipmentType()) && "Y".equals(s.getIsDefault()))
                .findFirst()
                .map(ShipmentDTO.Response::from)
                .orElse(null);
    }

    @Transactional
    public Long createShipment(String managerCode, ShipmentDTO.Request request) {
        if ("Y".equals(request.getIsDefault())) {
            clearDefaultShipment(managerCode, request.getShipmentType());
        }

        // 1. 임시 코드로 우선 저장하여 ID 생성
        Shipment shipment = Shipment.builder()
                .managerCode(managerCode)
                .shipmentCode("TEMP_" + java.util.UUID.randomUUID().toString().substring(0, 13))
                .shipmentType(request.getShipmentType())
                .shipmentName(request.getShipmentName())
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .supplierName(request.getSupplierName())
                .phone(request.getPhone())
                .mobile(request.getMobile())
                .shippingFee(request.getShippingFee())
                .returnFee(request.getReturnFee())
                .exchangeFee(request.getExchangeFee())
                .deliveryIslandYn(request.getDeliveryIslandYn())
                .deliveryIslandFee(request.getDeliveryIslandFee())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : "N")
                .build();

        shipment = shipmentRepository.save(shipment);

        // 2. ID를 기반으로 SHIP + 숫자6자리 코드 생성 (예: SHIP000001)
        String generatedCode = String.format("SHIP%06d", shipment.getId());
        shipment.updateShipmentCode(generatedCode);

        return shipment.getId();
    }

    @Transactional
    public void updateShipment(Long id, String managerCode, ShipmentDTO.Request request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다. ID: " + id));

        if ("Y".equals(request.getIsDefault()) && !"Y".equals(shipment.getIsDefault())) {
            clearDefaultShipment(managerCode, request.getShipmentType());
        }

        shipment.update(
                request.getShipmentType(),
                request.getShipmentName(),
                request.getZipcode(),
                request.getAddress(),
                request.getAddressDetail(),
                request.getSupplierName(),
                request.getPhone(),
                request.getMobile(),
                request.getShippingFee(),
                request.getReturnFee(),
                request.getExchangeFee(),
                request.getDeliveryIslandYn(),
                request.getDeliveryIslandFee(),
                request.getIsDefault() != null ? request.getIsDefault() : "N"
        );
    }

    @Transactional
    public void deleteShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다. ID: " + id));
        shipment.delete("SYSTEM"); // 또는 현재 로그인한 사용자 ID
    }

    private void clearDefaultShipment(String managerCode, String shipmentType) {
        List<Shipment> defaults = shipmentRepository.findByManagerCodeAndDeleteYn(managerCode, "N");
        for (Shipment s : defaults) {
            if (shipmentType.equals(s.getShipmentType()) && "Y".equals(s.getIsDefault())) {
                s.clearDefault();
            }
        }
    }
}
