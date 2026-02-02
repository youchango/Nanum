package com.nanum.user.point.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PointSearchDto {
    private Long memberId;
    private String memberName;
    private LocalDate startDate;
    private LocalDate endDate;
}
