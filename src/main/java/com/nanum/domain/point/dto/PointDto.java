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
    private String pointGubun;
    private String orderNo;
    private LocalDateTime createdAt;

    public PointDto(Point point) {
        this.pointId = point.getPointId();
        this.pointUse = point.getPointUse();
        this.pointBigo = point.getPointBigo();
        this.pointGubun = point.getPointGubun();
        this.orderNo = point.getOrderNo();
        this.memberCode = point.getMember().getMemberCode();
        this.createdAt = point.getCreatedAt();
    }
}
