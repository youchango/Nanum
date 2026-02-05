package com.nanum.domain.point.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PointSearchDto {
    private String memberCode;
    private String memberName;
    private LocalDate startDate;
    private LocalDate endDate;
}
