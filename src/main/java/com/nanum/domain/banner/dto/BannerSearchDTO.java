package com.nanum.domain.banner.dto;

import com.nanum.global.common.dto.SearchDTO;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 배너 목록 검색/페이지 DTO.
 * SearchDTO를 상속하여 공통 페이지/키워드 필드를 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BannerSearchDTO extends SearchDTO {

    /** 사이트 코드 필터 */
    private String siteCd;

    /** 사용 여부 필터 (Y / N / ALL) */
    private String searchUseYn;

    /** 게시 시작일 (해당 날짜 이후 시작하는 배너) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate searchStartDate;

    /** 게시 종료일 (해당 날짜 이전 종료하는 배너) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate searchEndDate;

    /** 검색 기준 (title 등) */
    private String searchType;

    /** 검색 키워드 */
    private String searchKeyword;

    /** 페이지당 데이터 수 (프론트 'size' 파라미터와 매핑) */
    private int size = 20;
}
