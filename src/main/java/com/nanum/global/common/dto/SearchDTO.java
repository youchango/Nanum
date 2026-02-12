package com.nanum.global.common.dto;

import lombok.Data;

/**
 * 검색 및 페이지 처리를 위한 공통 DTO 클래스입니다.
 */
@Data
public class SearchDTO {

    /** 현재 페이지 번호 (기본값 1) */
    private int page = 1;

    /** 페이지당 표시할 데이터 수 (기본값 10) */
    private int recordSize = 10;

    /** 화면 하단에 표시할 페이지 사이즈 (기본값 10) */
    private int pageSize = 10;

    /** 검색 키워드 (검색어) */
    private String keyword;

    /** 검색 키워드 (신규 규격) */
    private String searchKeyword;

    /** 검색 유형 (예: 이름, 아이디 등) */
    private String searchType;

    /** 전체 데이터 수 */
    private int totalRecordCount;

    /** 전체 페이지 수 */
    private int totalPageCount;

    /** 페이지 리스트의 시작 번호 */
    private int startPage;

    /** 페이지 리스트의 끝 번호 */
    private int endPage;

    /** 이전 페이지 존재 여부 */
    private boolean existPrevPage;

    /** 다음 페이지 존재 여부 */
    private boolean existNextPage;

    /** 승인 여부 (Y/N) */
    private String applyYn;

    /** 회원 구분 (U/B/V) */
    private String memberType;

    /** 관리자 구분 (MASTER/ADMIN/SCM) */
    private String managerType;

    /** 검색 조건 맵 (추가적인 검색 조건) */
    private java.util.Map<String, Object> params = new java.util.HashMap<>();

    /**
     * LIMIT 구문의 시작 위치(OFFSET)를 계산합니다.
     * 
     * @return OFFSET 값
     */
    public int getOffset() {
        return (page - 1) * recordSize;
    }

    /**
     * 전체 데이터 수에 따른 페이지들의 정보를 계산합니다.
     * 
     * @param totalRecordCount 전체 데이터 수
     */
    public void setPagination(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
        calculation();
    }

    private void calculation() {
        // 전체 페이지 수 계산
        totalPageCount = ((totalRecordCount - 1) / recordSize) + 1;

        // 현재 페이지가 전체 페이지 수보다 크다면 조정
        if (page > totalPageCount) {
            page = totalPageCount;
        }

        // 시작 페이지, 끝 페이지 계산
        startPage = ((page - 1) / pageSize) * pageSize + 1;
        endPage = startPage + pageSize - 1;

        // 끝 페이지가 전체 페이지 수보다 크다면 조정
        if (endPage > totalPageCount) {
            endPage = totalPageCount;
        }

        // 이전/다음 페이지 존재 여부
        existPrevPage = startPage != 1;
        existNextPage = (endPage * recordSize) < totalRecordCount;
    }
}
