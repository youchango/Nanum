package com.nanum.domain.point.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.nanum.domain.point.model.Point;

@Data
public class PointDto {
    private Long pointId;
    private Integer pointUse;
    private String pointBigo;
    private String memberCode;
    private Long paymentId;
    private LocalDateTime createdAt;

    public PointDto(Point point) {
        this.pointId = point.getPointId();
        this.pointUse = point.getPointUse();
        this.pointBigo = point.getPointBigo();
        this.memberCode = point.getMember().getMemberCode();
        this.paymentId = point.getPayment() != null ? point.getPayment().getPaymentId() : null;
        this.createdAt = point.getCreatedAt();
    }
}
