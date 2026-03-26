CREATE TABLE payment (
    payment_id             INT(11) NOT NULL AUTO_INCREMENT COMMENT '결제코드',
    order_id               INT(11) NOT NULL COMMENT '주문ID',
    site_cd                VARCHAR(20) NOT NULL COMMENT '사이트 코드',
    order_no               VARCHAR(50) NOT NULL COMMENT '주문 번호',
    member_code            VARCHAR(30) NOT NULL COMMENT '회원코드',
    total_price            DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '총주문금액',
    discount_price         DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '총할인금액',
    used_poINT             DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '포인트사용액',
    used_coupon            DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '쿠폰사용액',
    delivery_price         DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '배송비',
    payment_price          DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '실결제금액',
    payment_status         VARCHAR(20) NOT NULL COMMENT '결제상태',
    payment_method         VARCHAR(20) DEFAULT NULL COMMENT '결제수단',
    bank_name              VARCHAR(50) DEFAULT NULL COMMENT '은행명',
    bank_account_num       VARCHAR(50) DEFAULT NULL COMMENT '계좌번호',
    bank_account_name      VARCHAR(50) DEFAULT NULL COMMENT '예금주명',
    depositor_name         VARCHAR(50) DEFAULT NULL COMMENT '입금자명',
    deposit_deadline       DATETIME DEFAULT NULL COMMENT '입금기한',
    cancel_total_price     DECIMAL(19,4) DEFAULT 0.0000 COMMENT '총취소금액',
    cancel_coupon_price    DECIMAL(19,4) DEFAULT 0.0000 COMMENT '취소쿠폰액',
    cancel_poINT_price     DECIMAL(19,4) DEFAULT 0.0000 COMMENT '취소포인트액',
    cancel_delivery_price  DECIMAL(19,4) DEFAULT 0.0000 COMMENT '취소배송비',
    payment_key            VARCHAR(200) DEFAULT NULL COMMENT 'PG 결제키',
    payment_date           DATETIME DEFAULT NULL COMMENT '결제일',
    created_at             DATETIME NOT NULL DEFAULT current_timestamp() COMMENT '생성일',
    created_by             VARCHAR(50) DEFAULT NULL COMMENT '생성자',
    updated_at             DATETIME DEFAULT NULL COMMENT '수정일',
    updated_by             VARCHAR(50) DEFAULT NULL COMMENT '수정자',
    deleted_at             DATETIME DEFAULT NULL COMMENT '삭제일',
    deleted_by             VARCHAR(50) DEFAULT NULL COMMENT '삭제자',
    delete_yn              CHAR(1) NOT NULL DEFAULT 'N' COMMENT '삭제유무',
    PRIMARY KEY (payment_id),
    KEY idx_payment_status (payment_status),
    KEY fk_payment_member (member_code),
    KEY idx_payment_order (order_id),
    KEY idx_payment_site_order (site_cd,order_no),
    CONSTRAINT fk_payment_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='결제';

-- -------------------------------------------------------------------------------------------------
-- 2. 현금영수증 내역 테이블
-- -------------------------------------------------------------------------------------------------
CREATE TABLE cash_receipt (
    receipt_id             INT AUTO_INCREMENT COMMENT '현금영수증 ID',
    payment_id             INT NOT NULL COMMENT '결제코드',
    
    receipt_type           VARCHAR(20) NOT NULL COMMENT '종류 (INCOME_DEDUCTION 소득공제, EXPENDITURE_PROOF 지출증빙)',
    identity_num           VARCHAR(50) NOT NULL COMMENT '식별번호 (휴대폰, 사업자, 현금영수증카드번호)',
    
    issue_amount           DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '발행금액',
    supply_value           DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '공급가액',
    vat                    DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '부가세',
    
    receipt_status         VARCHAR(20) NOT NULL COMMENT '상태 (REQUESTED, ISSUED, CANCELLED, FAILED)',
    receipt_url            VARCHAR(255) NULL COMMENT '영수증 URL',
    issue_date             DATETIME NULL COMMENT '발행일',
    
    created_at             DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by             VARCHAR(50) NULL COMMENT '생성자',
    updated_at             DATETIME NULL COMMENT '수정일',
    updated_by             VARCHAR(50) NULL COMMENT '수정자',
    deleted_at             DATETIME NULL COMMENT '삭제일',
    deleted_by             VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn              CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    
    PRIMARY KEY (receipt_id),
    CONSTRAINT fk_cash_receipt_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id) ON DELETE CASCADE
) COMMENT '현금영수증 내역';

-- -------------------------------------------------------------------------------------------------
-- 3. 세금계산서 내역 테이블
-- -------------------------------------------------------------------------------------------------
CREATE TABLE tax_invoice (
    invoice_id             INT AUTO_INCREMENT COMMENT '세금계산서 ID',
    payment_id             INT NOT NULL COMMENT '결제코드',
    
    company_name           VARCHAR(100) NOT NULL COMMENT '상호명/법인명',
    ceo_name               VARCHAR(50) NOT NULL COMMENT '대표자명',
    business_reg_num       VARCHAR(20) NOT NULL COMMENT '사업자등록번호',
    business_type          VARCHAR(50) NULL COMMENT '업태',
    business_class         VARCHAR(50) NULL COMMENT '종목',
    company_address        VARCHAR(255) NULL COMMENT '사업장주소',
    email                  VARCHAR(100) NULL COMMENT '담당자 이메일',

    issue_amount           DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '발행금액',
    supply_value           DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '공급가액',
    vat                    DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '부가세',
    
    invoice_status         VARCHAR(20) NOT NULL COMMENT '상태 (REQUESTED, ISSUED, CANCELLED, FAILED)',
    invoice_url            VARCHAR(255) NULL COMMENT '계산서 URL',
    issue_date             DATETIME NULL COMMENT '발행일',
    
    created_at             DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by             VARCHAR(50) NULL COMMENT '생성자',
    updated_at             DATETIME NULL COMMENT '수정일',
    updated_by             VARCHAR(50) NULL COMMENT '수정자',
    deleted_at             DATETIME NULL COMMENT '삭제일',
    deleted_by             VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn              CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    
    PRIMARY KEY (invoice_id),
    CONSTRAINT fk_tax_invoice_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id) ON DELETE CASCADE
) COMMENT '세금계산서 내역';
