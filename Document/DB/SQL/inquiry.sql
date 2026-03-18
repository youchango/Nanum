-- Source: inquiry.sql
-- PRODUCT:상품, ORDER:주문, DELIVERY:배송, ETC:기타
CREATE TABLE inquiry (
    inquiry_id   INT AUTO_INCREMENT COMMENT '문의코드',
    site_cd      VARCHAR(20) NULL COMMENT '사이트코드',
    inquiry_type VARCHAR(20) NOT NULL COMMENT '문의구분 (PRODUCT:상품, ORDER:주문, DELIVERY:배송, ETC:기타)',
    product_id   INT NULL COMMENT '상품코드',
    order_no     VARCHAR(50) NULL COMMENT '주문번호',
    title        VARCHAR(100) NOT NULL COMMENT '제목',
    content      TEXT NOT NULL COMMENT '내용',
    answer       TEXT NULL COMMENT '답변',
    status       VARCHAR(20) NOT NULL COMMENT '처리상태',
    writer_code  VARCHAR(30) NOT NULL COMMENT '작성자(회원코드)',
    answerer_code VARCHAR(30) NULL COMMENT '답변자(회원코드)',
    answered_at  DATETIME NULL COMMENT '답변일',
    created_at             DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by             VARCHAR(50) NULL COMMENT '생성자',
    updated_at             DATETIME NULL COMMENT '수정일',
    updated_by             VARCHAR(50) NULL COMMENT '수정자',
    deleted_at             DATETIME NULL COMMENT '삭제일',
    deleted_by             VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn              CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (inquiry_id),
    INDEX idx_inquiry_status (status),
    CONSTRAINT fk_inquiry_writer FOREIGN KEY (writer_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_inquiry_answerer FOREIGN KEY (answerer_code) REFERENCES member (member_code) ON DELETE SET NULL
) COMMENT '문의';
