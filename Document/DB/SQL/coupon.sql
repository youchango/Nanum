-- 1. Coupon Master
CREATE TABLE coupon (
    coupon_id        INT AUTO_INCREMENT COMMENT '쿠폰ID',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    coupon_name      VARCHAR(100) NOT NULL COMMENT '쿠폰명',
    discount_type    VARCHAR(10) DEFAULT 'FIXED' NOT NULL COMMENT '할인타입(FIXED:정액, RATE:정률)',
    discount_value   INT NOT NULL COMMENT '할인값(원/%)',
    max_discount     INT NULL COMMENT '최대할인금액(정률시)',
    min_order_price  INT DEFAULT 0 NOT NULL COMMENT '최소주문금액',
    valid_start_date DATETIME NOT NULL COMMENT '유효시작일시',
    valid_end_date   DATETIME NOT NULL COMMENT '유효종료일시',
    target_member_type VARCHAR(10) DEFAULT 'ALL' NOT NULL COMMENT '발급대상회원등급(ALL, U:일반, B:기업, V:보훈)',
    target_product_id INT NULL COMMENT '특정상품적용(NULL이면 전체상품)',
    issue_limit      INT NULL COMMENT '총 발급제한수량(NULL이면 무제한)',
    member_issue_limit INT DEFAULT 1 NOT NULL COMMENT '1인당 발급제한수량',
    issue_count      INT DEFAULT 0 NOT NULL COMMENT '발급수량',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     VARCHAR(50) NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     VARCHAR(50) NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (coupon_id)
) COMMENT '쿠폰 마스터';

-- 2. Member Coupon (Issued Coupons)
CREATE TABLE member_coupon (
    issue_id         INT AUTO_INCREMENT COMMENT '발급ID',
    coupon_id        INT NOT NULL COMMENT '쿠폰ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    status           VARCHAR(10) DEFAULT 'UNUSED' NOT NULL COMMENT '상태(UNUSED, USED, EXPIRED)',
    used_at          DATETIME NULL COMMENT '사용일시',
    order_id         INT NULL COMMENT '사용주문ID',
    
    issued_at        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '발급일시',
    expired_at       DATETIME NOT NULL COMMENT '만료일시',
    
    PRIMARY KEY (issue_id),
    INDEX idx_mc_member (member_code),
    CONSTRAINT fk_mc_coupon FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id),
    CONSTRAINT fk_mc_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '회원 쿠폰 발급내역';
