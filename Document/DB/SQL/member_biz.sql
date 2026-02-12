CREATE TABLE member_biz (
    member_code      VARCHAR(30) PRIMARY KEY COMMENT '회원코드(FK)',
    business_number  VARCHAR(20) NOT NULL COMMENT '사업자등록번호',
    company_name     VARCHAR(100) NOT NULL COMMENT '상호명',
    ceo_name         VARCHAR(50) NOT NULL COMMENT '대표자명',
    business_type    VARCHAR(100) NULL COMMENT '업태',
    business_item    VARCHAR(100) NULL COMMENT '종목',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at       DATETIME NULL COMMENT '수정일',
    
    CONSTRAINT fk_member_biz_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '기업회원 상세정보';
