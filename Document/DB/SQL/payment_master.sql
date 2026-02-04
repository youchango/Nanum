CREATE TABLE payment_master (
    payment_id     INT AUTO_INCREMENT COMMENT '결제코드',
    member_code    VARCHAR(30) NOT NULL COMMENT '회원코드',
    payment_amount INT NOT NULL COMMENT '결제금액',
    used_point     INT DEFAULT 0 NOT NULL COMMENT '사용포인트',
    payment_status VARCHAR(20) NOT NULL COMMENT '결제상태',
    payment_method VARCHAR(20) NULL COMMENT '결제수단',
    payment_date   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '결제일',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     INT NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     INT NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     INT NULL COMMENT '삭제자',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (payment_id),
    INDEX idx_payment_status (payment_status),
    CONSTRAINT fk_payment_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '결제 master';
