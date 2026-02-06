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

CREATE TABLE basic (
    basic_id   INT AUTO_INCREMENT COMMENT '기본설정코드',
    type       VARCHAR(20) NOT NULL COMMENT '타입',
    content    VARCHAR(4000) NULL COMMENT '컨텐츠',
    updated_at DATETIME NULL COMMENT '수정일',
    updated_by INT NULL COMMENT '수정자',
    PRIMARY KEY (basic_id)
) COMMENT '기본설정';

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
    `reg_date`       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    `delete_yn`      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    `deleted_at`     DATETIME NULL COMMENT '삭제일시',
    `deleted_by`     VARCHAR(30) NULL COMMENT '삭제자(회원코드)',
    PRIMARY KEY (`file_id`),
    INDEX `idx_file_ref` (`reference_type`, `reference_id`),
    INDEX `idx_file_reg` (`reg_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='전사 통합 파일 관리';

CREATE TABLE member (
    id               INT AUTO_INCREMENT COMMENT 'ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
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
    category_id      INT NOT NULL COMMENT '카테고리ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명',
    price            INT DEFAULT 0 NOT NULL COMMENT '판매가',
    sale_price       INT DEFAULT 0 NULL COMMENT '할인가(실판매가)',
    status           VARCHAR(20) DEFAULT 'SALE' NOT NULL COMMENT '상태(SALE, STOP, SOLD_OUT)',
    description      TEXT NULL COMMENT '상품설명',
    view_count       INT DEFAULT 0 NOT NULL COMMENT '조회수',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    updated_at       DATETIME NULL COMMENT '수정일',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(30) NULL COMMENT '삭제자(회원코드)',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (product_id),
    INDEX idx_product_cat (category_id)
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

-- 1. Coupon Master
CREATE TABLE coupon (
    coupon_id        INT AUTO_INCREMENT COMMENT '쿠폰ID',
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
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    order_status     VARCHAR(20) DEFAULT 'PAY_WAIT' NOT NULL COMMENT '주문상태(PAY_WAIT, PAID, PREPARE, DELIVERY, COMPLETE, CANCEL, REFUND)',
    total_amount     INT DEFAULT 0 NOT NULL COMMENT '총주문금액',
    discount_amount  INT DEFAULT 0 NOT NULL COMMENT '할인금액',
    delivery_fee     INT DEFAULT 0 NOT NULL COMMENT '배송비',
    payment_amount   INT DEFAULT 0 NOT NULL COMMENT '실결제금액',
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
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명(스냅샷)',
    option_name      VARCHAR(100) NULL COMMENT '옵션명(스냅샷)',
    price            INT NOT NULL COMMENT '판매가(스냅샷)',
    discount_price   INT DEFAULT 0 NOT NULL COMMENT '할인금액',
    quantity         INT NOT NULL COMMENT '주문수량',
    sub_total        INT NOT NULL COMMENT '소계(가격*수량)',
    detail_status    VARCHAR(20) NULL COMMENT '개별상태(필요시 사용)',
    PRIMARY KEY (order_detail_id),
    CONSTRAINT fk_detail_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '주문 상세';

-- 1. Delivery
CREATE TABLE delivery (
    delivery_id      INT AUTO_INCREMENT COMMENT '배송ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    courier_company  VARCHAR(20) NULL COMMENT '택배사코드',
    tracking_number  VARCHAR(50) NULL COMMENT '운송장번호',
    status           VARCHAR(20) DEFAULT 'READY' NOT NULL COMMENT '배송상태(READY, SHIPPING, COMPLETE)',
    shipped_at       DATETIME NULL COMMENT '출고일시',
    completed_at     DATETIME NULL COMMENT '배송완료일시',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    updated_at       DATETIME NULL COMMENT '수정일',
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

CREATE TABLE point (
    point_id   INT AUTO_INCREMENT COMMENT '포인트코드',
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

CREATE TABLE banner (
    banner_id      INT AUTO_INCREMENT COMMENT '배너코드',
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
    created_by     VARCHAR(30) NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     VARCHAR(30) NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     VARCHAR(30) NULL COMMENT '삭제자',
    PRIMARY KEY (`banner_id`),
    INDEX `idx_banner_sort` (`banner_type`, `sort_order`),
    INDEX `idx_banner_date` (`start_datetime`, `end_datetime`)
) COMMENT '배너 관리';

CREATE TABLE `popup` (
    `popup_id`       INT AUTO_INCREMENT COMMENT '팝업코드',
    `title`          VARCHAR(100) NOT NULL COMMENT '팝업제목',
    `content_html`   TEXT NULL COMMENT '팝업내용(HTML)',
    `link_type`      VARCHAR(20) NULL COMMENT '링크 타입',
    `link_url`       VARCHAR(255) NULL COMMENT '링크 URL',
    `width`          INT DEFAULT 400 NOT NULL COMMENT '창 너비',
    `height`         INT DEFAULT 500 NOT NULL COMMENT '창 높이',
    `pos_x`          INT DEFAULT 0 NOT NULL COMMENT '위치 X좌표',
    `pos_y`          INT DEFAULT 0 NOT NULL COMMENT '위치 Y좌표',
    `close_type`     VARCHAR(20) DEFAULT 'DAY' NOT NULL COMMENT '닫기옵션 (NEVER, DAY, ONCE)',
    `device_type`    VARCHAR(10) DEFAULT 'ALL' NOT NULL COMMENT '노출기기 (PC, MOBILE, ALL)',
    `start_datetime` DATETIME NOT NULL COMMENT '게시 시작일시',
    `end_datetime`   DATETIME NOT NULL COMMENT '게시 종료일시',
    `use_yn`         CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용유무',
    `delete_yn`      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    `created_at`     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    `created_by`     VARCHAR(30) NULL COMMENT '생성자',
    `updated_at`     DATETIME NULL COMMENT '수정일',
    `updated_by`     VARCHAR(30) NULL COMMENT '수정자',
    `deleted_at`     DATETIME NULL COMMENT '삭제일',
    `deleted_by`     VARCHAR(30) NULL COMMENT '삭제자',
    PRIMARY KEY (popup_id),
    INDEX idx_popup_date (start_datetime, end_datetime)
) COMMENT '팝업 관리';

CREATE TABLE inquiry (
    inquiry_id   INT AUTO_INCREMENT COMMENT '문의코드',
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

CREATE TABLE content (
    content_id   INT AUTO_INCREMENT COMMENT '컨텐츠 코드',
    content_type INT NOT NULL COMMENT '구분(Code ID)',
    subject      VARCHAR(200) NOT NULL COMMENT '제목',
    content_body LONGTEXT NOT NULL COMMENT '내용',
    url_info     VARCHAR(255) NULL COMMENT 'URL 정보',
    updated_by   VARCHAR(30) NULL COMMENT '수정자(회원코드)',
    updated_at   DATETIME NULL COMMENT '수정일',
    created_by   VARCHAR(30) NULL COMMENT '생성자(회원코드)',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    delete_yn    CHAR(1) DEFAULT 'N' COMMENT '삭제 여부',
    deleted_at   DATETIME NULL COMMENT '삭제일',
    deleted_by   VARCHAR(30) NULL COMMENT '삭제자(회원코드)',
    PRIMARY KEY (content_id)
) COMMENT '컨텐츠';

-- -----------------------------------------------------
-- Product Wishlist
-- -----------------------------------------------------
CREATE TABLE product_wishlist (
    wishlist_id      INT AUTO_INCREMENT COMMENT '찜ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드(FK)',
    product_id       INT NOT NULL COMMENT '상품ID(FK)',
    reg_date         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    PRIMARY KEY (wishlist_id),
    UNIQUE KEY uq_wishlist_user_prod (member_code, product_id),
    CONSTRAINT fk_wishlist_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 찜 목록';

