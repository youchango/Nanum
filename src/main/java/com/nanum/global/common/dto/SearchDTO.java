package com.nanum.global.common.dto;

import lombok.Data;

/**
 * 寃??諛??섏씠吏?泥섎━瑜??꾪븳 怨듯넻 DTO ?대옒?ㅼ엯?덈떎.
 */
@Data
public class SearchDTO {

    /** ?꾩옱 ?섏씠吏 踰덊샇 (湲곕낯媛? 1) */
    private int page = 1;

    /** ?섏씠吏???쒖떆???곗씠????(湲곕낯媛? 10) */
    private int recordSize = 10;

    /** ?붾㈃ ?섎떒???쒖떆???섏씠吏 ?ъ씠利?(湲곕낯媛? 10) */
    private int pageSize = 10;

    /** 寃???ㅼ썙??(?덇굅??吏?? */
    private String keyword;
    
    /** 寃???ㅼ썙??(?좉퇋 ?쒖?) */
    private String searchKeyword;

    /** 寃???좏삎 (?? ?대쫫, ?꾩씠?? 而⑦뀗痢좎쑀???? */
    private String searchType;
    
    /** ?꾩껜 ?곗씠????*/
    private int totalRecordCount;
    
    /** ?꾩껜 ?섏씠吏 ??*/
    private int totalPageCount;
    
    /** ?섏씠吏 由ъ뒪?몄쓽 ?쒖옉 踰덊샇 */
    private int startPage;
    
    /** ?섏씠吏 由ъ뒪?몄쓽 ??踰덊샇 */
    private int endPage;
    
    /** ?댁쟾 ?섏씠吏 議댁옱 ?щ? */
    private boolean existPrevPage;
    
    /** ?ㅼ쓬 ?섏씠吏 議댁옱 ?щ? */
    private boolean existNextPage;

    /** 寃??議곌굔 留?(異붽??곸씤 寃??議곌굔) */
    private java.util.Map<String, Object> params = new java.util.HashMap<>();

    /**
     * LIMIT 援щЦ???쒖옉 ?꾩튂(OFFSET)瑜?怨꾩궛?⑸땲??
     * @return OFFSET 媛?
     */
    public int getOffset() {
        return (page - 1) * recordSize;
    }
    
    /**
     * ?꾩껜 ?곗씠???섏뿉 ?곕Ⅸ ?섏씠吏?ㅼ씠???뺣낫瑜?怨꾩궛?⑸땲??
     * @param totalRecordCount ?꾩껜 ?곗씠????
     */
    public void setPagination(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
        calculation();
    }
    
    private void calculation() {
        // ?꾩껜 ?섏씠吏 ??怨꾩궛
        totalPageCount = ((totalRecordCount - 1) / recordSize) + 1;
        
        // ?꾩옱 ?섏씠吏媛 ?꾩껜 ?섏씠吏 ?섎낫???щ㈃ 議곗젙
        if (page > totalPageCount) {
            page = totalPageCount;
        }
        
        // ?쒖옉 ?섏씠吏, ???섏씠吏 怨꾩궛
        startPage = ((page - 1) / pageSize) * pageSize + 1;
        endPage = startPage + pageSize - 1;
        
        // ???섏씠吏媛 ?꾩껜 ?섏씠吏 ?섎낫???щ㈃ 議곗젙
        if (endPage > totalPageCount) {
            endPage = totalPageCount;
        }
        
        // ?댁쟾/?ㅼ쓬 ?섏씠吏 議댁옱 ?щ?
        existPrevPage = startPage != 1;
        existNextPage = (endPage * recordSize) < totalRecordCount;
    }
}
