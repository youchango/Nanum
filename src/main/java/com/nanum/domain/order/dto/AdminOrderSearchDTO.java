package com.nanum.domain.order.dto;

import com.nanum.domain.order.model.OrderStatus;
import com.nanum.global.common.dto.SearchDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderSearchDTO extends SearchDTO {
    private String siteCd;
    private OrderStatus status;
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private java.time.LocalDate startDate;
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private java.time.LocalDate endDate;
}
