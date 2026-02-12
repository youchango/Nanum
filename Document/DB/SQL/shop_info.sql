-- -----------------------------------------------------
-- Shop Information
-- -----------------------------------------------------
CREATE TABLE shop_info (
    shop_key         INT AUTO_INCREMENT COMMENT '쇼핑몰Key',
    site_cd          VARCHAR(10) NOT NULL COMMENT '사이트코드',
    shop_type        VARCHAR(10) NOT NULL COMMENT '쇼핑몰타입',
    shop_name        VARCHAR(100) NOT NULL COMMENT '쇼핑몰명',
    shop_domain      VARCHAR(100) NULL COMMENT '도메인',
    shop_status      VARCHAR(10) DEFAULT 'R' NOT NULL COMMENT '상태(R:준비, O:운영, S:중지)',
    shop_mode        VARCHAR(2) DEFAULT 'C' NOT NULL COMMENT '모드',
    shop_corp        VARCHAR(100) NOT NULL COMMENT '법인명',
    shop_bsn         VARCHAR(15) NOT NULL COMMENT '사업자번호',
    shop_psn         VARCHAR(30) NULL COMMENT '통신판매업신고번호',
    shop_uptae       VARCHAR(50) NULL COMMENT '업태',
    shop_upjong      VARCHAR(50) NULL COMMENT '종목',
    shop_zipcode     VARCHAR(7) NULL COMMENT '우편번호',
    shop_addr1       VARCHAR(100) NULL COMMENT '주소1',
    shop_addr2       VARCHAR(100) NULL COMMENT '주소2',
    shop_phone       VARCHAR(20) NULL COMMENT '대표전화',
    shop_fax         VARCHAR(20) NULL COMMENT '팩스',
    shop_dam_name    VARCHAR(20) NULL COMMENT '담당자명',
    shop_dam_position VARCHAR(20) NULL COMMENT '담당자직급',
    shop_dam_phone   VARCHAR(20) NULL COMMENT '담당자연락처',
    shop_dam_email   VARCHAR(50) NULL COMMENT '담당자이메일',
    shop_bank_name   VARCHAR(20) NULL COMMENT '은행명',
    shop_bank_account_num VARCHAR(50) NULL COMMENT '계좌번호',
    shop_bank_account_name VARCHAR(20) NULL COMMENT '예금주',
    shop_set_product_use_max_point DECIMAL(19, 4) DEFAULT 0 NULL COMMENT '포인트사용최대치',
    shop_set_product_acc_point DECIMAL(19, 4) DEFAULT 0 NULL COMMENT '포인트적립율',
    shop_insert_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    PRIMARY KEY (shop_key)
) COMMENT '쇼핑몰 기본정보';
