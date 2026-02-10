CREATE TABLE payment_master (
    payment_id             INT AUTO_INCREMENT COMMENT '결제코드',
    order_id               INT NOT NULL COMMENT '주문ID',
    member_code            VARCHAR(30) NOT NULL COMMENT '회원코드',
    
    -- Payment Details
    total_price            DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '총주문금액',
    discount_price         DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '총할인금액',
    used_point             DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '포인트사용액',
    used_coupon            DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '쿠폰사용액',
    delivery_price         DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '배송비',
    payment_price          DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '실결제금액',

    payment_status         VARCHAR(20) NOT NULL COMMENT '결제상태',
    payment_method         VARCHAR(20) NULL COMMENT '결제수단',
    
    -- Bank Transfer Info (Virtual Account / Bank Transfer)
    bank_name              VARCHAR(50) NULL COMMENT '은행명',
    bank_account_num       VARCHAR(50) NULL COMMENT '계좌번호',
    bank_account_name      VARCHAR(50) NULL COMMENT '예금주명',
    depositor_name         VARCHAR(50) NULL COMMENT '입금자명',
    deposit_deadline       DATETIME NULL COMMENT '입금기한',
    
    -- Cancel / Refund Info
    cancel_total_price     DECIMAL(19,4) DEFAULT 0 NULL COMMENT '총취소금액',
    cancel_coupon_price    DECIMAL(19,4) DEFAULT 0 NULL COMMENT '취소쿠폰액',
    cancel_point_price     DECIMAL(19,4) DEFAULT 0 NULL COMMENT '취소포인트액',
    cancel_delivery_price  DECIMAL(19,4) DEFAULT 0 NULL COMMENT '취소배송비',

    payment_date           DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '결제일',
    created_at             DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by             VARCHAR(50) NULL COMMENT '생성자',
    updated_at             DATETIME NULL COMMENT '수정일',
    updated_by             VARCHAR(50) NULL COMMENT '수정자',
    deleted_at             DATETIME NULL COMMENT '삭제일',
    deleted_by             VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn              CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    
    PRIMARY KEY (payment_id),
    UNIQUE KEY uq_payment_order (order_id),
    INDEX idx_payment_status (payment_status),
    CONSTRAINT fk_payment_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '결제 master';
