-- Source: banner.sql
CREATE TABLE banner (
    banner_id      INT AUTO_INCREMENT COMMENT '배너코드',
    site_cd        VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    banner_type    VARCHAR(20) NOT NULL COMMENT '배너구분 (MAIN_TOP, SUB_MID)',
    link_type      VARCHAR(20) NULL COMMENT '링크 타입',
    link_url       VARCHAR(255) NULL COMMENT '링크 URL',
    sort_order     INT DEFAULT 1 NOT NULL COMMENT '노출순서',
    device_type    VARCHAR(10) DEFAULT 'ALL' NOT NULL COMMENT '노출기기 (PC, MOBILE, ALL)',
    start_datetime DATETIME NOT NULL COMMENT '게시 시작일시',
    end_datetime   DATETIME NOT NULL COMMENT '게시 종료일시',
    use_yn         CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용유무',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     INT NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     INT NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     INT NULL COMMENT '삭제자',
    PRIMARY KEY (banner_id),
    INDEX idx_banner_sort (banner_type, sort_order),
    INDEX idx_banner_date (start_datetime, end_datetime)
) COMMENT '배너 관리';


-- Source: basic.sql
CREATE TABLE basic (
    basic_id   INT AUTO_INCREMENT COMMENT '기본설정코드',
    type       VARCHAR(20) NOT NULL COMMENT '타입',
    content    VARCHAR(4000) NULL COMMENT '컨텐츠',
    updated_at DATETIME NULL COMMENT '수정일',
    updated_by INT NULL COMMENT '수정자',
    PRIMARY KEY (basic_id)
) COMMENT '기본설정';


-- Source: claim.sql
-- -----------------------------------------------------
-- Claim (Order Claim)
-- -----------------------------------------------------
CREATE TABLE claim (
    claim_id         INT AUTO_INCREMENT COMMENT '클레임ID',
    site_cd          VARCHAR(100) NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    claim_cd         VARCHAR(10) NOT NULL COMMENT '클레임코드',
    order_id         INT NOT NULL COMMENT '주문ID',
    order_seq        INT NULL COMMENT '주문순번',
    claim_type       VARCHAR(10) NOT NULL COMMENT '클레임타입',
    claim_gubun      VARCHAR(10) NULL COMMENT '클레임구분',
    claim_reason     TEXT NULL COMMENT '클레임사유',
    claim_status     VARCHAR(20) NOT NULL COMMENT '클레임상태',
    claim_date_entry DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '클레임신청일시',
    claim_check      VARCHAR(10) NULL COMMENT '확인여부',
    claim_date_check DATETIME NULL COMMENT '확인일시',
    claim_check_cd   VARCHAR(30) NULL COMMENT '확인자(ManagerCode)',
    claim_memo       TEXT NULL COMMENT '관리자메모',
    vbank_input_name VARCHAR(40) NULL COMMENT '가상계좌 입금자명',
    vbank_bank_name  VARCHAR(40) NULL COMMENT '가상계좌 은행명',
    vbank_num        VARCHAR(40) NULL COMMENT '가상계좌번호',
    refund_type      VARCHAR(20) NULL COMMENT '환불타입',
    return_zipcode   VARCHAR(10) NULL COMMENT '반품우편번호',
    return_address   VARCHAR(200) NULL COMMENT '반품주소',
    return_detail    VARCHAR(200) NULL COMMENT '반품상세주소',
    refund_check     VARCHAR(10) NULL COMMENT '환불확인',
    refund_price     DECIMAL(19, 4) NULL COMMENT '환불금액',
    claim_cnt        INT NULL COMMENT '클레임횟수',
    product_id       INT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    exchange_zipcode VARCHAR(10) NULL COMMENT '교환우편번호',
    exchange_address VARCHAR(200) NULL COMMENT '교환주소',
    exchange_detail  VARCHAR(200) NULL COMMENT '교환상세주소',
    refund_status    VARCHAR(20) NULL COMMENT '환불상태',
    refund_manager_cd VARCHAR(30) NULL COMMENT '환불담당자(ManagerCode)',
    created_by       VARCHAR(30) NULL COMMENT '등록자(MemberCode)',
    PRIMARY KEY (claim_id),
    CONSTRAINT fk_claim_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_claim_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE,
    CONSTRAINT fk_claim_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE SET NULL
) COMMENT '주문 클레임';


-- Source: code.sql
CREATE TABLE code (
    code_id    INT AUTO_INCREMENT COMMENT '코드관리 PK',
    code_type  VARCHAR(20) NOT NULL COMMENT '코드타입',
    depth      INT NULL COMMENT 'depth',
    upper      INT NULL COMMENT 'upper',
    code_name  VARCHAR(100) NOT NULL COMMENT '코드명',
    use_yn     CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용유무',
    delete_yn  CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by INT NULL COMMENT '생성자',
    updated_at DATETIME NULL COMMENT '수정일',
    updated_by INT NULL COMMENT '수정자',
    deleted_at DATETIME NULL COMMENT '삭제일',
    deleted_by INT NULL COMMENT '삭제자',
    PRIMARY KEY (code_id),
    INDEX idx_code_type (code_type),
    CONSTRAINT fk_code_upper FOREIGN KEY (upper) REFERENCES code (code_id) ON DELETE SET NULL
) COMMENT '코드관리';


-- Source: content.sql
CREATE TABLE content (
    content_id   INT AUTO_INCREMENT COMMENT '컨텐츠 코드',
    site_cd      VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    content_type INT NOT NULL COMMENT '구분(Code ID)',
    subject      VARCHAR(200) NOT NULL COMMENT '제목',
    content_body LONGTEXT NOT NULL COMMENT '내용',
    url_info     VARCHAR(255) NULL COMMENT 'URL 정보',
    updated_by   BIGINT NULL COMMENT '수정자',
    updated_at   DATETIME NULL COMMENT '수정일',
    created_by   BIGINT NULL COMMENT '생성자',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    deleted_yn   CHAR(1) DEFAULT 'N' COMMENT '삭제 여부',
    deleted_at   DATETIME NULL COMMENT '삭제일',
    deleted_by   BIGINT NULL COMMENT '삭제자',
    PRIMARY KEY (content_id)
) COMMENT '컨텐츠';


-- Source: coupon.sql
-- 1. Coupon Master
CREATE TABLE coupon (
    coupon_id        INT AUTO_INCREMENT COMMENT '쿠폰ID',
    site_cd          VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    coupon_name      VARCHAR(100) NOT NULL COMMENT '쿠폰명',
    discount_type    VARCHAR(10) DEFAULT 'FIXED' NOT NULL COMMENT '할인타입(FIXED:정액, RATE:정률)',
    discount_value   INT NOT NULL COMMENT '할인값(원/%)',
    max_discount     INT NULL COMMENT '최대할인금액(정률시)',
    min_order_price  INT DEFAULT 0 NOT NULL COMMENT '최소주문금액',
    
    valid_start_date DATETIME NOT NULL COMMENT '유효시작일시',
    valid_end_date   DATETIME NOT NULL COMMENT '유효종료일시',
    target_type      VARCHAR(20) DEFAULT 'ALL' NOT NULL COMMENT '적용대상(ALL:전체, USER:개인, BIZ:기업)',
    issue_limit      INT NULL COMMENT '발급제한수량',
    issue_count      INT DEFAULT 0 NOT NULL COMMENT '발급수량',
    
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by       INT NULL COMMENT '생성자',
    
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


-- Source: delivery.sql
-- 1. Delivery
CREATE TABLE delivery (
    delivery_id      INT AUTO_INCREMENT COMMENT '배송ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호',
    order_detail_id  INT NOT NULL COMMENT '주문상세ID',
    delivery_corp    VARCHAR(200) NULL COMMENT '택배사명',
    tracking_number  VARCHAR(50) NULL COMMENT '운송장번호',
    status           VARCHAR(20) DEFAULT 'READY' NOT NULL COMMENT '배송상태(READY, SHIPPING, COMPLETE)',
    
    shipped_at       DATETIME NULL COMMENT '출고일시',
    completed_at     DATETIME NULL COMMENT '배송완료일시',

    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(20) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(20) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(20) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    
    PRIMARY KEY (delivery_id),
    UNIQUE KEY uq_delivery_order (order_id),
    FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '배송 정보';

-- 2. Address Book
CREATE TABLE address_book (
    address_id       INT AUTO_INCREMENT COMMENT '배송지ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    address_name     VARCHAR(50) NULL COMMENT '배송지명(우리집, 회사 등)',
    dept_name        VARCHAR(100) NULL COMMENT '부서/프로젝트명 (기업회원용)',
    receiver_name    VARCHAR(50) NOT NULL COMMENT '수령자명',
    receiver_phone   VARCHAR(20) NOT NULL COMMENT '수령자연락처',
    zipcode          VARCHAR(10) NOT NULL COMMENT '우편번호',
    address          VARCHAR(255) NOT NULL COMMENT '주소',
    address_detail   VARCHAR(255) NOT NULL COMMENT '상세주소',
    is_default       CHAR(1) DEFAULT 'N' NOT NULL COMMENT '기본배송지여부',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    
    PRIMARY KEY (address_id),
    INDEX idx_addr_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '배송지 목록';


-- Source: file_store.sql
CREATE TABLE `file_store` (
    `file_id`        VARCHAR(36) NOT NULL COMMENT '파일ID (UUID)',
    `reference_type` VARCHAR(50) NOT NULL COMMENT '참조구분 (PRODUCT, CATEGORY, BANNER, POPUP, REVIEW, INQUIRY)',
    `reference_id`   VARCHAR(50) NOT NULL COMMENT '참조ID (Entity PK)',
    
    `org_name`       VARCHAR(255) NOT NULL COMMENT '원본파일명',
    `save_name`      VARCHAR(255) NOT NULL COMMENT '저장파일명(UUID포함)',
    `path`           VARCHAR(500) NOT NULL COMMENT '전체 URL 또는 상대경로',
    `ext`            VARCHAR(10) NOT NULL COMMENT '확장자',
    `size`           BIGINT DEFAULT 0 NOT NULL COMMENT '파일크기(Byte)',
    
    `is_main`        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '대표이미지 여부(Y/N)',
    `display_order`  INT DEFAULT 0 NOT NULL COMMENT '노출순서',
    
    `created_at`       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    `created_by`       VARCHAR(20) NOT NULL COMMENT '등록자',
    `updated_at`       DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    `updated_by`       VARCHAR(20) DEFAULT NULL COMMENT '수정자',
    `deleted_at`       DATETIME DEFAULT NULL COMMENT '삭제일시',
    `deleted_by`       VARCHAR(20) DEFAULT NULL COMMENT '삭제자',
    `delete_yn`        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부(Y/N)',
    
    PRIMARY KEY (`file_id`),
    INDEX `idx_file_ref` (`reference_type`, `reference_id`),
    INDEX `idx_file_reg` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='전사 통합 파일 관리';


-- Source: inquiry.sql
CREATE TABLE inquiry (
    inquiry_id   INT AUTO_INCREMENT COMMENT '문의코드',
    site_cd      VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    inquiry_type INT NOT NULL COMMENT '문의구분 (코드ID)',
    title        VARCHAR(100) NOT NULL COMMENT '제목',
    content      TEXT NOT NULL COMMENT '내용',
    answer       TEXT NULL COMMENT '답변',
    status       VARCHAR(20) NOT NULL COMMENT '처리상태',
    writer_code  VARCHAR(30) NOT NULL COMMENT '작성자(회원코드)',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    answerer_code VARCHAR(30) NULL COMMENT '답변자(회원코드)',
    answered_at  DATETIME NULL COMMENT '답변일',
    deleted_by   VARCHAR(30) NULL COMMENT '삭제자(회원코드)',
    deleted_at   DATETIME NULL COMMENT '삭제일',
    delete_yn    CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (inquiry_id),
    INDEX idx_inquiry_status (status),
    CONSTRAINT fk_inquiry_writer FOREIGN KEY (writer_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_inquiry_answerer FOREIGN KEY (answerer_code) REFERENCES member (member_code) ON DELETE SET NULL
) COMMENT '문의';


-- Source: inventory_history.sql
-- [NEW] Inventory History (Product Stock In/Out) - Master/Detail Structure

-- 1. Inventory History Master
-- 재고 변동의 공통 정보(날짜, 담당자, 전체 비고)를 관리
CREATE TABLE inventory_history_master (
    history_id       INT AUTO_INCREMENT COMMENT '이력마스터ID',
    history_date     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '입출고일시',
    memo             VARCHAR(500) NULL COMMENT '전체비고',
    created_by       INT NULL COMMENT '담당자(관리자ID)',
    
    PRIMARY KEY (history_id),
    INDEX idx_hist_date (history_date)
) COMMENT '재고 입출고 이력 마스터';

-- 2. Inventory History Detail
-- 개별 상품 및 옵션의 수량 변동 내역
CREATE TABLE inventory_history_detail (
    detail_id        INT AUTO_INCREMENT COMMENT '상세ID',
    history_id       INT NOT NULL COMMENT '이력마스터ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    type             VARCHAR(20) NOT NULL COMMENT '구분(IN:입고, OUT:출고, RETURN:반품, ADJUST:조정)',
    quantity         INT NOT NULL COMMENT '변동수량',
    prev_quantity    INT NOT NULL COMMENT '변동전재고(창고재고 기준)',
    curr_quantity    INT NOT NULL COMMENT '변동후재고(창고재고 기준)',
    memo             VARCHAR(500) NULL COMMENT '개별비고',
    
    PRIMARY KEY (detail_id),
    CONSTRAINT fk_detail_master FOREIGN KEY (history_id) REFERENCES inventory_history_master (history_id) ON DELETE CASCADE,
    CONSTRAINT fk_detail_product FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT fk_detail_option FOREIGN KEY (option_id) REFERENCES product_option (option_id)
) COMMENT '재고 입출고 이력 상세';


-- Source: manager.sql
-- -----------------------------------------------------
-- Manager (Admin)
-- -----------------------------------------------------
CREATE TABLE manager (
    manager_seq      INT AUTO_INCREMENT COMMENT '관리자SEQ',
    manager_code     VARCHAR(30) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    site_cd          VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    manager_id       VARCHAR(20) NOT NULL COMMENT '아이디',
    password         VARCHAR(200) NOT NULL COMMENT '비밀번호',
    login_fail_count INT DEFAULT 0 NULL COMMENT '로그인실패횟수',
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    manager_name     VARCHAR(50) NOT NULL COMMENT '이름',
    manager_email    VARCHAR(50) NOT NULL COMMENT '이메일',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    apply_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '승인여부',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    description      VARCHAR(200) NULL COMMENT '설명',
    regist_by        VARCHAR(20) NOT NULL COMMENT '등록자',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    update_by        VARCHAR(20) NOT NULL COMMENT '수정자',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    login_date       DATETIME NULL COMMENT '최근로그인일시',
    mb_type          VARCHAR(20) DEFAULT '' NOT NULL COMMENT '관리자유형(MASTER, SCM, ADMIN)',
    PRIMARY KEY (manager_seq),
    UNIQUE KEY uq_manager_id (manager_id),
    UNIQUE KEY uq_manager_code (manager_code)
) COMMENT '관리자 정보';

-- -----------------------------------------------------
-- Manager Menu
-- -----------------------------------------------------
CREATE TABLE manager_menu (
    menu_seq         INT AUTO_INCREMENT COMMENT '메뉴SEQ',
    parent_menu_seq  INT NULL COMMENT '상위메뉴SEQ',
    menu_name        VARCHAR(100) NOT NULL COMMENT '메뉴명',
    program_url      VARCHAR(100) NULL COMMENT '프로그램URL',
    display_yn       CHAR(1) NOT NULL COMMENT '노출여부',
    display_order    INT NULL COMMENT '표시순서',
    regist_by        VARCHAR(200) NOT NULL COMMENT '등록자',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    update_by        VARCHAR(200) NOT NULL COMMENT '수정자',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    program_parameter VARCHAR(100) DEFAULT '' NOT NULL COMMENT '파라미터',
    PRIMARY KEY (menu_seq)
) COMMENT '관리자 메뉴';

-- -----------------------------------------------------
-- Manager Auth Group
-- -----------------------------------------------------
CREATE TABLE manager_auth_group (
    auth_group_seq   INT AUTO_INCREMENT COMMENT '권한그룹SEQ',
    auth_group_name  VARCHAR(100) NOT NULL COMMENT '권한그룹명',
    use_yn           CHAR(1) NOT NULL COMMENT '사용여부',
    regist_by        VARCHAR(200) NOT NULL COMMENT '등록자',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    update_by        VARCHAR(200) NOT NULL COMMENT '수정자',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    PRIMARY KEY (auth_group_seq)
) COMMENT '관리자 권한 그룹';

-- -----------------------------------------------------
-- Manager Menu Group Mapping
-- -----------------------------------------------------
CREATE TABLE manager_menu_group (
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    menu_seq         INT NOT NULL COMMENT '메뉴SEQ',
    regist_by        VARCHAR(200) NOT NULL COMMENT '등록자',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    PRIMARY KEY (auth_group_seq, menu_seq),
    CONSTRAINT fk_mmg_auth FOREIGN KEY (auth_group_seq) REFERENCES manager_auth_group (auth_group_seq) ON DELETE CASCADE,
    CONSTRAINT fk_mmg_menu FOREIGN KEY (menu_seq) REFERENCES manager_menu (menu_seq) ON DELETE CASCADE
) COMMENT '관리자 메뉴 권한 매핑';

-- -----------------------------------------------------
-- Manager SCM Information
-- -----------------------------------------------------

CREATE TABLE manager_scm (
    manager_code          VARCHAR(20) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    brand_name            VARCHAR(50) NOT NULL COMMENT '브랜드명',
    scm_ceo               VARCHAR(50) NOT NULL COMMENT '대표자명',
    scm_corp              VARCHAR(100) NOT NULL COMMENT '법인명',
    scm_type              VARCHAR(10) DEFAULT 'CORP' NOT NULL COMMENT '사업자구분(개인/법인)/ CORP, INDIV',
    scm_bsn               VARCHAR(15) NOT NULL COMMENT '사업자등록번호',
    scm_psn               VARCHAR(30) NULL COMMENT '통신판매업신고번호',
    scm_uptae             VARCHAR(50) NULL COMMENT '업태',
    scm_upjong            VARCHAR(50) NULL COMMENT '업종',
    scm_zipcode           VARCHAR(7) NULL COMMENT '본사우편번호',
    scm_addr1             VARCHAR(100) NULL COMMENT '본사주소',
    scm_addr2             VARCHAR(100) NULL COMMENT '본사상세주소',
    scm_phone             VARCHAR(20) NULL COMMENT '대표전화',
    scm_fax               VARCHAR(20) NULL COMMENT '팩스번호',
    scm_dam_name          VARCHAR(20) NULL COMMENT '담당자명',
    scm_dam_position      VARCHAR(20) NULL COMMENT '담당자직급',
    scm_dam_phone         VARCHAR(20) NULL COMMENT '담당자연락처',
    scm_dam_email         VARCHAR(50) NULL COMMENT '담당자이메일',
    scm_bank_name         VARCHAR(50) NOT NULL COMMENT '은행명',
    scm_bank_account_num  VARCHAR(200) NOT NULL COMMENT '계좌번호(암호화)',
    scm_bank_account_name VARCHAR(50) NOT NULL COMMENT '예금주',
    shipping_zipcode      VARCHAR(10) COMMENT '출고지우편번호',
    shipping_addr1        VARCHAR(200) COMMENT '출고지주소',
    shipping_addr2        VARCHAR(200) COMMENT '출고지상세주소',
    return_zipcode        VARCHAR(10) COMMENT '반품지우편번호',
    return_addr1          VARCHAR(200) COMMENT '반품지주소',
    return_addr2          VARCHAR(200) COMMENT '반품지상세주소',
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    created_by            VARCHAR(20) NOT NULL COMMENT '생성자(manager_code)',
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    updated_by            VARCHAR(20) NOT NULL COMMENT '수정자(manager_code)',
    delete_yn             CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    CONSTRAINT PK_MANAGER_SCM PRIMARY KEY (manager_code),
    CONSTRAINT FK_MANAGER_SCM_BASE FOREIGN KEY (manager_code) REFERENCES manager (manager_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SCM 관리자 상세정보';

-- Source: member.sql
CREATE TABLE member (
    id               INT AUTO_INCREMENT COMMENT 'ID',
    member_code      VARCHAR(20) NOT NULL COMMENT '회원코드',
    member_id        VARCHAR(50) NOT NULL COMMENT '회원아이디',
    member_name      VARCHAR(50) NOT NULL COMMENT '회원명',
    password         VARCHAR(255) NOT NULL COMMENT '비밀번호',
    business_number  VARCHAR(20) NULL COMMENT '사업자번호',
    phone            VARCHAR(20) NULL COMMENT '전화번호',
    mobile_phone     VARCHAR(20) NOT NULL COMMENT '휴대전화',
    zipcode          VARCHAR(10) NOT NULL COMMENT '우편주소',
    address          VARCHAR(255) NOT NULL COMMENT '주소',
    address_detail   VARCHAR(255) NOT NULL COMMENT '상세주소',
    email            VARCHAR(100) NULL COMMENT '이메일',
    memo             VARCHAR(2000) NULL COMMENT '메모',
    role             VARCHAR(20) NOT NULL COMMENT '권한',
    member_type      CHAR(1) NOT NULL COMMENT '회원구분',
    login_fail_count INT DEFAULT 0 NOT NULL COMMENT '로그인 실패횟수',
    withdraw_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '탈퇴유무',
    withdraw_at      DATETIME NULL COMMENT '탈퇴일',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at       DATETIME NULL COMMENT '수정일',
    sms_yn           CHAR(1) DEFAULT 'N' NOT NULL COMMENT 'SMS 수신 여부',
    email_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '이메일 수신 여부',
    PRIMARY KEY (id),
    UNIQUE KEY uq_member_code (member_code),
    UNIQUE KEY uq_member_id (member_id),
    INDEX idx_member_mobile (mobile_phone)
) COMMENT '회원';


-- Source: member_biz.sql
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


-- Source: order.sql
-- 1. Cart (Shopping Cart)
CREATE TABLE cart (
    cart_id          INT AUTO_INCREMENT COMMENT '장바구니ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    quantity         INT DEFAULT 1 NOT NULL COMMENT '수량',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    updated_at       DATETIME NULL COMMENT '수정일',
    
    PRIMARY KEY (cart_id),
    INDEX idx_cart_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '장바구니';

-- 2. Order Master
CREATE TABLE order_master (
    order_id         INT AUTO_INCREMENT COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호(UUID)',
    site_cd          VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    order_status     VARCHAR(20) DEFAULT 'PAY_WAIT' NOT NULL COMMENT '주문상태(PAY_WAIT, PAID, PREPARE, DELIVERY, COMPLETE, CANCEL, REFUND)',
    
    -- Amount Info
    total_price      DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '총주문금액',
    discount_price   DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '할인금액',
    used_point       DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '포인트사용액',
    used_coupon      DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '쿠폰사용액',
    delivery_price   DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '배송비',
    payment_price    DECIMAL(19,4) DEFAULT 0 NOT NULL COMMENT '실결제금액',
    
    -- Receiver Info
    receiver_name    VARCHAR(50) NOT NULL COMMENT '수령자명',
    receiver_phone   VARCHAR(20) NOT NULL COMMENT '수령자연락처',
    receiver_zipcode  VARCHAR(10) NOT NULL COMMENT '우편번호',
    receiver_address  VARCHAR(255) NOT NULL COMMENT '주소',
    receiver_detail   VARCHAR(255) NOT NULL COMMENT '상세주소',
    delivery_memo     VARCHAR(200) NULL COMMENT '배송메모',
    tracking_number   VARCHAR(50) NULL COMMENT '운송장번호',
    
    ordered_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '주문일시',
    updated_at       DATETIME NULL COMMENT '수정일시',
    
    PRIMARY KEY (order_id),
    UNIQUE KEY uq_order_no (order_no),
    INDEX idx_order_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code)
) COMMENT '주문 마스터';

-- 3. Order Detail (Line Items)
CREATE TABLE order_detail (
    order_detail_id  INT AUTO_INCREMENT COMMENT '주문상세ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    order_seq        INT NOT NULL COMMENT '주문순번',
    site_cd          VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명(스냅샷)',
    option_name      VARCHAR(100) NULL COMMENT '옵션명(스냅샷)',
    
    product_price    DECIMAL(19,4) NOT NULL COMMENT '상품가격',
    option_price     DECIMAL(19,4) DEFAULT 0 COMMENT '옵션가격',
    quantity         INT NOT NULL COMMENT '주문수량',
    total_price      DECIMAL(19,4) NOT NULL COMMENT '총금액(구 sub_total)',
    
    order_status     VARCHAR(20) DEFAULT 'PAY_WAIT' NOT NULL COMMENT '주문상태',
    
    delivery_num     VARCHAR(100) NULL COMMENT '송장번호',
    delivery_corp    VARCHAR(200) NULL COMMENT '택배사명',
    delivery_date_start DATETIME NULL COMMENT '배송시작일시',
    delivery_date_end   DATETIME NULL COMMENT '배송종료일시',
    
    claim_id         INT NULL COMMENT '문의ID',
    claim_cd         VARCHAR(10) NULL COMMENT '문의코드',
    claim_type       VARCHAR(10) NULL COMMENT '문의타입',
    
    cancel_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '취소여부',
    cancel_date      DATETIME NULL COMMENT '취소일시',
    cancel_price     DECIMAL(19,4) NULL COMMENT '취소금액',
    
    refund_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '환불여부',
    refund_date      DATETIME NULL COMMENT '환불일시',
    refund_price     DECIMAL(19,4) NULL COMMENT '환불금액',
    
    pickup_date_start DATETIME NULL COMMENT '반품 수거시작일시',
    pickup_date_end   DATETIME NULL COMMENT '반품 수거종료일시',

    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    created_by       VARCHAR(20) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(20) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(20) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    
    PRIMARY KEY (order_detail_id),
    CONSTRAINT fk_detail_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '주문 상세';


-- Source: payment_master.sql
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


-- Source: point.sql
CREATE TABLE point (
    point_id   INT AUTO_INCREMENT COMMENT '포인트코드',
    site_cd    VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    point_use  INT NOT NULL COMMENT '사용/적립 포인트',
    point_bigo VARCHAR(255) NULL COMMENT '포인트 상세 이력',
    member_code VARCHAR(30) NOT NULL COMMENT '회원코드',
    created_by INT NULL COMMENT '등록자',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    payment_id INT NULL COMMENT '결제코드',
    PRIMARY KEY (point_id),
    CONSTRAINT fk_point_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_point_payment FOREIGN KEY (payment_id) REFERENCES payment_master (payment_id) ON DELETE SET NULL
) COMMENT '포인트';


-- Source: popup.sql
CREATE TABLE popup (
    popup_id       INT AUTO_INCREMENT COMMENT '팝업코드',
    site_cd        VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    title          VARCHAR(100) NOT NULL COMMENT '팝업제목',
    content_html   TEXT NULL COMMENT '팝업내용(HTML)',
    link_type      VARCHAR(20) NULL COMMENT '링크 타입',
    link_url       VARCHAR(255) NULL COMMENT '링크 URL',
    width          INT DEFAULT 400 NOT NULL COMMENT '창 너비',
    height         INT DEFAULT 500 NOT NULL COMMENT '창 높이',
    pos_x          INT DEFAULT 0 NOT NULL COMMENT '위치 X좌표',
    pos_y          INT DEFAULT 0 NOT NULL COMMENT '위치 Y좌표',
    close_type     VARCHAR(20) DEFAULT 'DAY' NOT NULL COMMENT '닫기옵션 (NEVER, DAY, ONCE)',
    device_type    VARCHAR(10) DEFAULT 'ALL' NOT NULL COMMENT '노출기기 (PC, MOBILE, ALL)',
    start_datetime DATETIME NOT NULL COMMENT '게시 시작일시',
    end_datetime   DATETIME NOT NULL COMMENT '게시 종료일시',
    use_yn         CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용유무',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     INT NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     INT NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     INT NULL COMMENT '삭제자',
    PRIMARY KEY (popup_id),
    INDEX idx_popup_date (start_datetime, end_datetime)
) COMMENT '팝업 관리';


-- Source: product.sql
-- 1. Product Category
CREATE TABLE product_category (
    category_id      INT AUTO_INCREMENT COMMENT '카테고리ID',
    parent_id        INT NULL COMMENT '상위 카테고리ID',
    category_name    VARCHAR(100) NOT NULL COMMENT '카테고리명',
    depth            INT DEFAULT 1 NOT NULL COMMENT '뎁스',
    display_order    INT DEFAULT 0 NOT NULL COMMENT '노출순서',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at       DATETIME NULL COMMENT '수정일',
    
    PRIMARY KEY (category_id)
) COMMENT '상품 카테고리';

-- 2. Product Master
CREATE TABLE product (
    product_id       INT AUTO_INCREMENT COMMENT '상품ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명',
    brand_name       VARCHAR(100) NULL COMMENT '브랜드명',
    supply_price     INT DEFAULT 0 NOT NULL COMMENT '공급가',
    map_price        INT DEFAULT 0 NOT NULL COMMENT '지도가',
    standard_price   INT DEFAULT 0 NULL COMMENT '판매기준가',
    status           VARCHAR(20) DEFAULT 'SALE' NOT NULL COMMENT '상태(SALE, STOP, SOLD_OUT)',
    description      TEXT NULL COMMENT '상품설명',
    view_count       INT DEFAULT 0 NOT NULL COMMENT '조회수',
    
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(20) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(20) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(20) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    
    PRIMARY KEY (product_id)
) COMMENT '상품 마스터';

-- 3. Product Option (SKU)
CREATE TABLE product_option (
    option_id        INT AUTO_INCREMENT COMMENT '옵션ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_name      VARCHAR(100) NOT NULL COMMENT '옵션명',
    extra_price      INT DEFAULT 0 NOT NULL COMMENT '추가금액',
    stock_quantity   INT DEFAULT 0 NOT NULL COMMENT '재고수량',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    
    PRIMARY KEY (option_id),
    CONSTRAINT fk_option_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 옵션';

-- 4. Product Category Mapping (N:M)
CREATE TABLE product_category_by (
    product_id       INT NOT NULL COMMENT '상품ID',
    category_id      INT NOT NULL COMMENT '카테고리ID',
    
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pcb_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_pcb_category FOREIGN KEY (category_id) REFERENCES product_category (category_id) ON DELETE CASCADE
) COMMENT '상품-카테고리 매핑';

-- 5. Product Site (Multi-site Price/Display)
CREATE TABLE product_site (
    ps_id            INT AUTO_INCREMENT COMMENT 'PS Key',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NOT NULL COMMENT '옵션ID',
    site_cd          VARCHAR(100) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    view_yn          CHAR(1) DEFAULT 'N' NOT NULL COMMENT '노출여부',
    a_price          DECIMAL(19, 4) DEFAULT 0.0000 NOT NULL COMMENT 'A등급 가격',
    b_price          DECIMAL(19, 4) DEFAULT 0.0000 NOT NULL COMMENT 'B등급 가격',
    c_price          DECIMAL(19, 4) DEFAULT 0.0000 NOT NULL COMMENT 'C등급 가격',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    pdt_click        INT DEFAULT 0 NOT NULL COMMENT '클릭횟수',
    PRIMARY KEY (ps_id),
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_ps_option FOREIGN KEY (option_id) REFERENCES product_option (option_id) ON DELETE CASCADE
) COMMENT '상품 사이트별 정보';

-- 5. Product Stock (Warehouse)
CREATE TABLE product_stock (
    stock_id         INT AUTO_INCREMENT COMMENT '재고ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    stock_quantity   INT DEFAULT 0 NOT NULL COMMENT '재고수량(실재고)',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at       DATETIME NULL COMMENT '수정일',
    
    PRIMARY KEY (stock_id),
    UNIQUE KEY uq_stock_prod_opt (product_id, option_id),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_option FOREIGN KEY (option_id) REFERENCES product_option (option_id) ON DELETE CASCADE
) COMMENT '상품 재고(창고)';


-- Source: product_biz_mapping.sql
-- Product Biz Mapping (B2B Only)
CREATE TABLE product_biz_mapping (
    mapping_id       INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    member_code      VARCHAR(30) NOT NULL COMMENT '기업회원코드',
    discount_rate    INT DEFAULT 0 COMMENT '할인율',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_prod_biz (product_id, member_code),
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '기업전용상품매핑';


-- Source: product_wishlist.sql
CREATE TABLE product_wishlist (
    wishlist_id      INT AUTO_INCREMENT COMMENT '찜ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드(FK)',
    product_id       INT NOT NULL COMMENT '상품ID(FK)',
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    PRIMARY KEY (wishlist_id),
    UNIQUE KEY uq_wishlist_user_prod (member_code, product_id),
    CONSTRAINT fk_wishlist_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 찜 목록';


-- Source: shop_info.sql
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


-- Source: wishlist.sql
-- -----------------------------------------------------
-- Wishlist (Product Wishlist)
-- -----------------------------------------------------
CREATE TABLE wishlist (
    wishlist_id      INT AUTO_INCREMENT COMMENT '찜ID',
    site_cd          VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드(FK)',
    product_id       INT NOT NULL COMMENT '상품ID(FK)',
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    PRIMARY KEY (wishlist_id),
    UNIQUE KEY uq_wishlist_user_prod (member_code, product_id),
    CONSTRAINT fk_wishlist_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 찜 목록';


