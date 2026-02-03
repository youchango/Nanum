-- ==========================================
-- Nanum Shopping Mall Platform Initialization
-- Integrated Schema (Reflecting all requirements)
-- ==========================================

-- 1. Configuration & Code Tables

-- Code (System Codes)
CREATE TABLE IF NOT EXISTS code (
    code_id    INT AUTO_INCREMENT PRIMARY KEY COMMENT '코드ID',
    code_type  VARCHAR(20) NOT NULL COMMENT '코드유형',
    depth      INT DEFAULT 1 COMMENT 'Depth',
    upper      INT NULL COMMENT '상위코드ID',
    code_name  VARCHAR(100) NOT NULL COMMENT '코드명',
    use_yn     CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    sort_order INT DEFAULT 0 COMMENT '정렬순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    FOREIGN KEY (upper) REFERENCES code (code_id) ON DELETE SET NULL
) COMMENT '공통코드';

-- Basic Settings
CREATE TABLE IF NOT EXISTS basic (
    basic_id   INT AUTO_INCREMENT PRIMARY KEY,
    type       VARCHAR(20) NOT NULL COMMENT '설정유형',
    content    VARCHAR(4000) NULL COMMENT '설정값',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
) COMMENT '기본설정';

-- File Store
CREATE TABLE IF NOT EXISTS file_store (
    file_id    VARCHAR(36) PRIMARY KEY COMMENT '파일ID(UUID)',
    group_id   VARCHAR(36) NOT NULL COMMENT '그룹ID',
    org_name   VARCHAR(255) NOT NULL COMMENT '원본파일명',
    save_name  VARCHAR(255) NOT NULL COMMENT '저장파일명',
    size       BIGINT NOT NULL COMMENT '파일크기',
    ext        VARCHAR(10) NOT NULL COMMENT '확장자',
    path       VARCHAR(255) NOT NULL COMMENT '저장경로',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '파일저장소';


-- 2. Member Domain (Updated: PK member_code)

-- Member
CREATE TABLE IF NOT EXISTS member (
    id               INT AUTO_INCREMENT COMMENT 'ID',
    member_code      VARCHAR(20) NOT NULL COMMENT '회원코드',
    member_id     VARCHAR(50) NOT NULL COMMENT '회원아이디',
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
    member_type      VARCHAR(20) NOT NULL COMMENT '회원구분',
    login_fail_count INT DEFAULT 0 NOT NULL COMMENT '로그인 실패횟수',
    withdraw_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '탈퇴유무',
    withdraw_at      DATETIME NULL COMMENT '탈퇴일',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at       DATETIME NULL COMMENT '수정일',
    sms_yn           CHAR(1) DEFAULT 'N' NOT NULL COMMENT 'SMS 수신 여부',
    email_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '이메일 수신 여부',
    PRIMARY KEY (id),
    UNIQUE KEY uq_member_code (member_code),
    UNIQUE KEY uq_member_login (member_login),
    INDEX idx_member_mobile (mobile_phone)
) COMMENT '회원';


-- Member Biz (Corporate Info)
CREATE TABLE IF NOT EXISTS member_biz (
    member_code      VARCHAR(30) PRIMARY KEY COMMENT '회원코드(FK)',
    company_name     VARCHAR(100) NOT NULL COMMENT '회사명',
    ceo_name         VARCHAR(50) NOT NULL COMMENT '대표자명',
    business_type    VARCHAR(100) COMMENT '업태',
    business_item    VARCHAR(100) COMMENT '종목',
    approval_status  VARCHAR(20) DEFAULT 'PENDING' COMMENT '승인상태',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '기업회원 상세';

-- Address Book (Delivery Addresses)
CREATE TABLE IF NOT EXISTS address_book (
    address_id       INT AUTO_INCREMENT PRIMARY KEY,
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    address_name     VARCHAR(50) COMMENT '배송지명',
    receiver_name    VARCHAR(50) NOT NULL COMMENT '수령인',
    receiver_phone   VARCHAR(20) NOT NULL COMMENT '수령인연락처',
    zipcode          VARCHAR(10) NOT NULL,
    address          VARCHAR(255) NOT NULL,
    address_detail   VARCHAR(255) NOT NULL,
    is_default       CHAR(1) DEFAULT 'N',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '배송지 목록';

-- Point
CREATE TABLE IF NOT EXISTS point (
    point_id   INT AUTO_INCREMENT PRIMARY KEY,
    member_code VARCHAR(30) NOT NULL COMMENT '회원코드',
    point_amt  INT NOT NULL COMMENT '포인트(증감)',
    reason     VARCHAR(255) COMMENT '사유',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '포인트 이력';


-- 3. Product Domain

-- Product Category
CREATE TABLE IF NOT EXISTS product_category (
    category_id      INT AUTO_INCREMENT PRIMARY KEY,
    parent_id        INT COMMENT '상위카테고리ID',
    category_name    VARCHAR(100) NOT NULL,
    depth            INT DEFAULT 1,
    display_order    INT DEFAULT 0,
    use_yn           CHAR(1) DEFAULT 'Y',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES product_category (category_id)
) COMMENT '상품카테고리';

-- Product
CREATE TABLE IF NOT EXISTS product (
    product_id       INT AUTO_INCREMENT PRIMARY KEY,
    category_id      INT NOT NULL,
    product_name     VARCHAR(200) NOT NULL,
    price            INT NOT NULL DEFAULT 0,
    sale_price       INT DEFAULT 0,
    status           VARCHAR(20) DEFAULT 'SALE' COMMENT '상태(SALE, STOP, SOLD_OUT)',
    description      TEXT,
    thumbnail_url    VARCHAR(500),
    view_count       INT DEFAULT 0,
    delete_yn        CHAR(1) DEFAULT 'N',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES product_category (category_id)
) COMMENT '상품';

-- Product Option
CREATE TABLE IF NOT EXISTS product_option (
    option_id        INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    option_name      VARCHAR(100) NOT NULL,
    extra_price      INT DEFAULT 0,
    stock_quantity   INT DEFAULT 0,
    use_yn           CHAR(1) DEFAULT 'Y',
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품옵션';

-- Product Image
CREATE TABLE IF NOT EXISTS product_image (
    image_id         INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    image_url        VARCHAR(500) NOT NULL,
    image_type       VARCHAR(20) DEFAULT 'DETAIL',
    display_order    INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품이미지';

-- Product Biz Mapping (B2B Only)
CREATE TABLE IF NOT EXISTS product_biz_mapping (
    mapping_id       INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    member_code      VARCHAR(30) NOT NULL COMMENT '기업회원코드',
    discount_rate    INT DEFAULT 0 COMMENT '할인율',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_prod_biz (product_id, member_code),
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '기업전용상품매핑';

-- Inventory History
CREATE TABLE IF NOT EXISTS inventory_history (
    history_id       INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    option_id        INT,
    type             VARCHAR(20) NOT NULL COMMENT 'IN/OUT/RETURN/ADJUST',
    quantity         INT NOT NULL,
    before_qty       INT NOT NULL,
    after_qty        INT NOT NULL,
    ref_id           VARCHAR(50) COMMENT '관련 주문/입고 번호',
    memo             VARCHAR(500),
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '재고이력';


-- 4. Order & Cart Domain

-- Cart
CREATE TABLE IF NOT EXISTS cart (
    cart_id          INT AUTO_INCREMENT PRIMARY KEY,
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    product_id       INT NOT NULL,
    option_id        INT,
    quantity         INT DEFAULT 1,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '장바구니';

-- Coupon
CREATE TABLE IF NOT EXISTS coupon (
    coupon_id        INT AUTO_INCREMENT PRIMARY KEY,
    coupon_name      VARCHAR(100) NOT NULL,
    discount_type    VARCHAR(10) DEFAULT 'FIXED' COMMENT 'FIXED/RATE',
    discount_value   INT NOT NULL,
    min_order_price  INT DEFAULT 0,
    valid_start_date DATETIME NOT NULL,
    valid_end_date   DATETIME NOT NULL,
    issue_limit      INT,
    issue_count      INT DEFAULT 0,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '쿠폰';

-- Member Coupon
CREATE TABLE IF NOT EXISTS member_coupon (
    issue_id         INT AUTO_INCREMENT PRIMARY KEY,
    coupon_id        INT NOT NULL,
    member_code      VARCHAR(30) NOT NULL,
    status           VARCHAR(10) DEFAULT 'UNUSED',
    used_at          DATETIME,
    issued_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    expired_at       DATETIME,
    FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id),
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '회원보유쿠폰';

-- Order
CREATE TABLE IF NOT EXISTS order_master (
    order_id         INT AUTO_INCREMENT PRIMARY KEY,
    order_no         VARCHAR(50) NOT NULL UNIQUE COMMENT '주문번호(UUID)',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    order_status     VARCHAR(20) DEFAULT 'PAYMENT_WAIT',
    
    total_amount     BIGINT DEFAULT 0 NOT NULL,
    discount_amount  BIGINT DEFAULT 0,
    delivery_fee     INT DEFAULT 0,
    payment_amount   BIGINT DEFAULT 0,
    
    recipient_name   VARCHAR(50) NOT NULL,
    recipient_phone  VARCHAR(20) NOT NULL,
    shipping_zipcode VARCHAR(10),
    shipping_address VARCHAR(255),
    shipping_detail  VARCHAR(255),
    delivery_memo    VARCHAR(200),
    tracking_number  VARCHAR(50),
    
    ordered_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_code) REFERENCES member (member_code)
) COMMENT '주문';

-- Order Detail
CREATE TABLE IF NOT EXISTS order_detail (
    detail_id        INT AUTO_INCREMENT PRIMARY KEY,
    order_id         INT NOT NULL,
    product_id       INT NOT NULL,
    option_id        INT,
    
    product_name     VARCHAR(200) NOT NULL,
    option_name      VARCHAR(100),
    price            INT NOT NULL,
    quantity         INT NOT NULL,
    sub_total        BIGINT NOT NULL,
    detail_status    VARCHAR(20),
    
    FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '주문상세';

-- Delivery
CREATE TABLE IF NOT EXISTS delivery (
    delivery_id      INT AUTO_INCREMENT PRIMARY KEY,
    order_id         INT NOT NULL UNIQUE,
    courier_company  VARCHAR(30),
    tracking_number  VARCHAR(50),
    status           VARCHAR(20) DEFAULT 'READY',
    shipped_at       DATETIME,
    completed_at     DATETIME,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '배송';

-- Payment
CREATE TABLE IF NOT EXISTS payment_master (
    payment_id       INT AUTO_INCREMENT PRIMARY KEY,
    order_id         INT NOT NULL,
    member_code      VARCHAR(30) NOT NULL,
    payment_method   VARCHAR(20),
    payment_amount   BIGINT NOT NULL,
    payment_status   VARCHAR(20) NOT NULL,
    paid_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES order_master (order_id),
    FOREIGN KEY (member_code) REFERENCES member (member_code)
) COMMENT '결제';


-- 5. Customer Service & Marketing

-- Banner
CREATE TABLE IF NOT EXISTS banner (
    banner_id      INT AUTO_INCREMENT PRIMARY KEY,
    banner_type    VARCHAR(20) NOT NULL,
    title          VARCHAR(100),
    image_file     VARCHAR(255),
    link_url       VARCHAR(255),
    sort_order     INT DEFAULT 1,
    start_datetime DATETIME,
    end_datetime   DATETIME,
    use_yn         CHAR(1) DEFAULT 'Y',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '배너';

-- Popup
CREATE TABLE IF NOT EXISTS popup (
    popup_id       INT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(100) NOT NULL,
    content_image  VARCHAR(255),
    link_url       VARCHAR(255),
    pos_x          INT DEFAULT 0,
    pos_y          INT DEFAULT 0,
    width          INT DEFAULT 400,
    height         INT DEFAULT 500,
    start_datetime DATETIME,
    end_datetime   DATETIME,
    use_yn         CHAR(1) DEFAULT 'Y',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '팝업';

-- Inquiry (1:1)
CREATE TABLE IF NOT EXISTS inquiry (
    inquiry_id     INT AUTO_INCREMENT PRIMARY KEY,
    inquiry_type   VARCHAR(20) COMMENT '문의유형 코드',
    title          VARCHAR(200) NOT NULL,
    content        TEXT NOT NULL,
    member_code    VARCHAR(30) NOT NULL,
    answer         TEXT,
    status         VARCHAR(20) DEFAULT 'WAITING',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    answered_at    DATETIME,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '1:1문의';

-- Content (Notice, FAQ)
CREATE TABLE IF NOT EXISTS content (
    content_id     INT AUTO_INCREMENT PRIMARY KEY,
    content_type   VARCHAR(20) NOT NULL, -- NOTICE, FAQ
    subject        VARCHAR(200) NOT NULL,
    body           TEXT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    view_count     INT DEFAULT 0
) COMMENT '게시판';
