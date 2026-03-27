package com.nanum.domain.point.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.nanum.domain.point.model.Point;
import com.nanum.domain.point.model.PointType;

@Data
public class PointDto {
    private Long pointId;
    private Integer pointUse;
    private String pointBigo;
    private String memberCode;
    private PointType pointType;
    private String orderNo;
    private LocalDateTime createdAt;

    public PointDto(Point point) {
        this.pointId = point.getPointId();
        this.pointUse = point.getPointUse();
        this.pointBigo = point.getPointBigo();
        this.pointType = point.getPointType();
        this.orderNo = point.getOrderNo();
        this.memberCode = point.getMember().getMemberCode();
        this.createdAt = point.getCreatedAt();
    }
}
