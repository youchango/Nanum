package com.nanum.domain.shipment.dto;

import com.nanum.domain.shipment.model.Shipment;
import lombok.*;

import java.math.BigDecimal;

public class ShipmentDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String shipmentCode;
        private String shipmentType;
        private String shipmentName;
        private String zipcode;
        private String address;
        private String addressDetail;
        private String supplierName;
        private String phone;
        private String mobile;
        private BigDecimal shippingFee;
        private BigDecimal returnFee;
        private BigDecimal exchangeFee;
        private String deliveryIslandYn;
        private BigDecimal deliveryIslandFee;
        private String isDefault;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String managerCode;
        private String shipmentCode;
        private String shipmentType;
        private String shipmentName;
        private String zipcode;
        private String address;
        private String addressDetail;
        private String supplierName;
        private String phone;
        private String mobile;
        private BigDecimal shippingFee;
        private BigDecimal returnFee;
        private BigDecimal exchangeFee;
        private String deliveryIslandYn;
        private BigDecimal deliveryIslandFee;
        private String isDefault;

        public static Response from(Shipment shipment) {
            return Response.builder()
                    .id(shipment.getId())
                    .managerCode(shipment.getManagerCode())
                    .shipmentCode(shipment.getShipmentCode())
                    .shipmentType(shipment.getShipmentType())
                    .shipmentName(shipment.getShipmentName())
                    .zipcode(shipment.getZipcode())
                    .address(shipment.getAddress())
                    .addressDetail(shipment.getAddressDetail())
                    .supplierName(shipment.getSupplierName())
                    .phone(shipment.getPhone())
                    .mobile(shipment.getMobile())
                    .shippingFee(shipment.getShippingFee())
                    .returnFee(shipment.getReturnFee())
                    .exchangeFee(shipment.getExchangeFee())
                    .deliveryIslandYn(shipment.getDeliveryIslandYn())
                    .deliveryIslandFee(shipment.getDeliveryIslandFee())
                    .isDefault(shipment.getIsDefault())
                    .build();
        }
    }
}
