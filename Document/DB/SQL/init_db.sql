
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
    created_by VARCHAR(50) NULL COMMENT '생성자',
    updated_at DATETIME NULL COMMENT '수정일',
    updated_by VARCHAR(50) NULL COMMENT '수정자',
    deleted_at DATETIME NULL COMMENT '삭제일',
    deleted_by VARCHAR(50) NULL COMMENT '삭제자',
    PRIMARY KEY (code_id),
    INDEX idx_code_type (code_type),
    CONSTRAINT fk_code_upper FOREIGN KEY (upper) REFERENCES code (code_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '코드관리';


-- Source: shop_info.sql
-- -----------------------------------------------------
-- Shop Information
-- -----------------------------------------------------
CREATE TABLE shop_info (
    shop_key         INT AUTO_INCREMENT COMMENT '쇼핑몰Key',
    site_cd          VARCHAR(20) NOT NULL COMMENT '사이트코드',
    shop_type        VARCHAR(10) NOT NULL COMMENT '쇼핑몰타입',
    shop_name        VARCHAR(100) NOT NULL COMMENT '쇼핑몰명',
    shop_domain      VARCHAR(100) NULL COMMENT '도메인',
    shop_status      VARCHAR(10) DEFAULT 'R' NOT NULL COMMENT '상태(R:준비, O:운영, S:중지)',
    shop_mode        VARCHAR(10) DEFAULT 'DEV' NOT NULL COMMENT '모드',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '쇼핑몰 기본정보';




-- Source: member.sql
CREATE TABLE member (
    id               INT AUTO_INCREMENT COMMENT 'ID',
    member_code      VARCHAR(20) NOT NULL COMMENT '회원코드',
    member_id        VARCHAR(50) NOT NULL COMMENT '회원아이디',
    member_name      VARCHAR(50) NOT NULL COMMENT '회원명',
    password         VARCHAR(255) NOT NULL COMMENT '비밀번호',
    phone            VARCHAR(20) NULL COMMENT '전화번호',
    mobile_phone     VARCHAR(20) NOT NULL COMMENT '휴대전화',
    zipcode          VARCHAR(10) NOT NULL COMMENT '우편주소',
    address          VARCHAR(255) NOT NULL COMMENT '주소',
    address_detail   VARCHAR(255) NOT NULL COMMENT '상세주소',
    email            VARCHAR(100) NULL COMMENT '이메일',
    memo             VARCHAR(2000) NULL COMMENT '메모',
    role             VARCHAR(20) NOT NULL COMMENT '권한',
    member_type      CHAR(1) NOT NULL COMMENT '회원구분 (U:일반, B:기업, V:보훈)',
    login_fail_count INT DEFAULT 0 NOT NULL COMMENT '로그인 실패횟수',
    apply_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '승인여부',
    withdraw_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '탈퇴유무',
    withdraw_at      DATETIME NULL COMMENT '탈퇴일',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at       DATETIME NULL COMMENT '수정일',
    marketing_yn     CHAR(1) DEFAULT 'N' NOT NULL COMMENT '마케팅수신정보동의여부',
    PRIMARY KEY (id),
    UNIQUE KEY uq_member_code (member_code),
    UNIQUE KEY uq_member_id (member_id),
    INDEX idx_member_mobile (mobile_phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '회원';


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품 카테고리';

-- 2. Product Master
CREATE TABLE product (
    product_id       INT AUTO_INCREMENT COMMENT '상품ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명',
    brand_name       VARCHAR(100) NULL COMMENT '브랜드명',
    supply_price     INT DEFAULT 0 NOT NULL COMMENT '공급가',
    map_price        INT DEFAULT 0 NULL COMMENT '지도가',
    retail_price     INT DEFAULT 0 NULL COMMENT '소비자가',
    suggested_price  INT DEFAULT 0 NULL COMMENT '권장판매가',
    option_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '옵션여부',
    status           VARCHAR(20) DEFAULT 'SALE' NOT NULL COMMENT '상태(SALE, STOP, SOLD_OUT)',
    safety_stock	 INT DEFAULT 0 NULL COMMENT '적정재고',
    stock_quantity   INT DEFAULT 0 NOT NULL COMMENT '재고수량',
    description      TEXT NULL COMMENT '상품설명',
    view_count       INT DEFAULT 0 NOT NULL COMMENT '조회수',

    review_yn 			CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '리뷰작성가능여부',
    delivery_way		VARCHAR(20) NULL COMMENT '배송방법(택배, 우편(소포/등기), 직접전달(화물배달), 퀵서비스, 배송필요없음, 상담예약)',
    delivery_area 		VARCHAR(20) NULL COMMENT '배송지역(전국, 전국(제주/도서산간지역 제외), 서울, 인천, 광주, 대구, 부산, 울산, 경기, 강원, 충남, 충북, 경남, 경북, 전남, 전북, 제주, 서울/경기, 서울/경기/대전, 충북/충남, 경북/경남, 전북/전남, 부산/울산, 서울/경기/제주도서산간 제외지역)',
    delivery_type 		VARCHAR(20) NULL COMMENT '배송비정책(FREE:무료, PAY:유료, COND:조건부무료)',
    bundle_shipping_yn CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '묶음배송 가능 여부 (Y: 가능, N: 불가)',
    delivery_policy_type VARCHAR(20) NOT NULL DEFAULT 'MAX' COMMENT 'MAX: 상품의 최대 배송비 / MIN: 상품의 최소 배송비',
    delivery_min_order_fee	DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '최소주문금액',
    outbound_shipment_code VARCHAR(30) NULL COMMENT '출고지 코드',
	inbound_shipment_code  VARCHAR(30) NULL COMMENT '입고지 코드',
    delivery_fee 		DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '기본 배송비',
    return_fee 			DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '반품 배송비(편도)',
    exchange_fee 		DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '교환 배송비(왕복)',
    delivery_island_yn	CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '제주/도서산간 배송여부',
    delivery_island_fee	DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '제주/도서산간 배송비',

    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(50) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    apply_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '승인여부',
    PRIMARY KEY (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품 마스터';

-- 3. Product Option (SKU)
CREATE TABLE product_option (
    option_id        INT AUTO_INCREMENT COMMENT '옵션ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_title1    VARCHAR(50) NULL COMMENT '옵션분류1(예:색상)',
    option_name1     VARCHAR(100) NULL COMMENT '옵션값1(예:블랙)',
    option_title2    VARCHAR(50) NULL COMMENT '옵션분류2(예:사이즈)',
    option_name2     VARCHAR(100) NULL COMMENT '옵션값2(예:XL)',
    option_title3    VARCHAR(50) NULL COMMENT '옵션분류3(예:추가항목)',
    option_name3     VARCHAR(100) NULL COMMENT '옵션값3(예:각인)',
    extra_price      INT DEFAULT 0 NOT NULL COMMENT '추가금액',
    stock_quantity   INT DEFAULT 0 NOT NULL COMMENT '재고수량',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    PRIMARY KEY (option_id),
    CONSTRAINT fk_option_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품 옵션';

-- 4. Product Category Mapping (N:M)
CREATE TABLE product_category_by (
    product_id       INT NOT NULL COMMENT '상품ID',
    category_id      INT NOT NULL COMMENT '카테고리ID',
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pcb_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_pcb_category FOREIGN KEY (category_id) REFERENCES product_category (category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품-카테고리 매핑';

-- 5. Product Site (Multi-site Price/Display)
CREATE TABLE product_site (
    ps_id            INT AUTO_INCREMENT COMMENT 'PS Key',
    product_id       INT NOT NULL COMMENT '상품ID',
    site_cd          VARCHAR(100) NULL COMMENT '사이트코드',
    view_yn          CHAR(1) DEFAULT 'N' NOT NULL COMMENT '노출여부',
    sale_price       INT DEFAULT 0 NULL COMMENT '판매가',
    a_price          DECIMAL(19, 4) DEFAULT 0.0000 NOT NULL COMMENT 'A등급 가격',
    b_price          DECIMAL(19, 4) DEFAULT 0.0000 NOT NULL COMMENT 'B등급 가격',
    c_price          DECIMAL(19, 4) DEFAULT 0.0000 NOT NULL COMMENT 'C등급 가격',
    point_rate       DECIMAL(10, 4) DEFAULT 0.0000 NOT NULL COMMENT '포인트 적립률',
    pdt_click        INT DEFAULT 0 NOT NULL COMMENT '클릭횟수',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(50) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (ps_id),
    UNIQUE KEY uq_ps_prod_site (product_id, site_cd),
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품 사이트별 정보';

-- 7. Product Stock (Warehouse)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품 재고(창고)';

-- 8. Product Delivery (Quantity-based Policy)
CREATE TABLE product_delivery (
    pd_id           INT AUTO_INCREMENT COMMENT '배송비규칙ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    min_quantity     INT NOT NULL COMMENT '최소수량(또는 반복단위)',
    max_quantity     INT NULL COMMENT '최대수량(NULL이면 해당 수량 이상 또는 반복)',
    delivery_fee     DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '배송비',
    
    PRIMARY KEY (pd_id),
    CONSTRAINT fk_pd_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 수량별 배송비 정책';


-- Product Review
CREATE TABLE product_review (
    review_id        INT AUTO_INCREMENT PRIMARY KEY,
    site_cd          VARCHAR(20) NOT NULL COMMENT '사이트코드',
    product_id       INT NOT NULL,
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    title            VARCHAR(255) NOT NULL COMMENT '리뷰제목',
    content          TEXT NOT NULL COMMENT '리뷰내용',
    rating           INT NOT NULL COMMENT '별점',
    like_count       INT DEFAULT 0 COMMENT '좋아요',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    updated_at       DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    updated_by       VARCHAR(50) DEFAULT NULL COMMENT '수정자',
    deleted_at       DATETIME DEFAULT NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) DEFAULT NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부(Y/N)',
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품리뷰';

-- Product Review Like
CREATE TABLE product_review_like (
    like_id          INT AUTO_INCREMENT PRIMARY KEY,
    review_id        INT NOT NULL COMMENT '리뷰ID',
    site_cd          VARCHAR(20) NOT NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES product_review (review_id) ON DELETE CASCADE,
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    UNIQUE KEY uq_review_like (review_id, member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '상품리뷰 좋아요';

-- Source: file_store.sql
CREATE TABLE file_store (
    file_id        VARCHAR(36) NOT NULL COMMENT '파일ID (UUID)',
    reference_type VARCHAR(50) NOT NULL COMMENT '참조구분 (PRODUCT, CATEGORY, BANNER, POPUP, REVIEW, INQUIRY)',
    reference_id   VARCHAR(50) NOT NULL COMMENT '참조ID (Entity PK)',
    org_name       VARCHAR(255) NOT NULL COMMENT '원본파일명',
    save_name      VARCHAR(255) NOT NULL COMMENT '저장파일명(UUID포함)',
    path           VARCHAR(500) NOT NULL COMMENT '전체 URL 또는 상대경로',
    ext            VARCHAR(10) NOT NULL COMMENT '확장자',
    size           BIGINT DEFAULT 0 NOT NULL COMMENT '파일크기(Byte)',
    
    is_main        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '대표이미지 여부(Y/N)',
    display_order  INT DEFAULT 0 NOT NULL COMMENT '노출순서',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NOT NULL COMMENT '등록자',
    updated_at       DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    updated_by       VARCHAR(50) DEFAULT NULL COMMENT '수정자',
    deleted_at       DATETIME DEFAULT NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) DEFAULT NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부(Y/N)',
    
    PRIMARY KEY (file_id),
    INDEX idx_file_ref (reference_type, reference_id),
    INDEX idx_file_reg (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='전사 통합 파일 관리';


-- Source: banner.sql
CREATE TABLE banner (
    banner_id      INT AUTO_INCREMENT COMMENT '배너코드',
    title          VARCHAR(100) NOT NULL COMMENT '배너제목',
    site_cd        VARCHAR(20) NULL COMMENT '사이트코드',
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
    created_by     VARCHAR(50) NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     VARCHAR(50) NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     VARCHAR(50) NULL COMMENT '삭제자',
    PRIMARY KEY (banner_id),
    INDEX idx_banner_sort (banner_type, sort_order),
    INDEX idx_banner_date (start_datetime, end_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '배너 관리';



-- Source: content.sql
CREATE TABLE content (
    content_id   INT AUTO_INCREMENT COMMENT '컨텐츠 코드',
    site_cd      VARCHAR(20) NULL COMMENT '사이트코드',
    content_type VARCHAR(50) NOT NULL COMMENT 'NOTICE, FAQ',
    subject      VARCHAR(200) NOT NULL COMMENT '제목',
    content_body LONGTEXT NOT NULL COMMENT '내용',
    url_info     VARCHAR(255) NULL COMMENT 'URL 정보',
    created_by   VARCHAR(50) NULL COMMENT '생성자',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_by   VARCHAR(50) NULL COMMENT '수정자',
    updated_at   DATETIME NULL COMMENT '수정일',
    deleted_by   VARCHAR(50) NULL COMMENT '삭제자',
    deleted_at   DATETIME NULL COMMENT '삭제일',
    delete_yn    CHAR(1) DEFAULT 'N' COMMENT '삭제유무',
    PRIMARY KEY (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '공지사항 게시판 테이블';


-- Source: popup.sql
CREATE TABLE popup (
    popup_id       INT AUTO_INCREMENT COMMENT '팝업코드',
    site_cd        VARCHAR(20) NULL COMMENT '사이트코드',
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
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     VARCHAR(50) NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     VARCHAR(50) NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (popup_id),
    INDEX idx_popup_date (start_datetime, end_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '팝업 관리';



-- Source: coupon.sql
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
    target_type      VARCHAR(20) DEFAULT 'ALL' NOT NULL COMMENT '적용대상(ALL:전체, USER:개인, BIZ:기업)',
    issue_limit      INT NULL COMMENT '발급제한수량',
    issue_count      INT DEFAULT 0 NOT NULL COMMENT '발급수량',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     VARCHAR(50) NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     VARCHAR(50) NULL COMMENT '수정자',
    deleted_at     DATETIME NULL COMMENT '삭제일',
    deleted_by     VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '쿠폰 마스터';

-- 2. Member Coupon (Issued Coupons)
CREATE TABLE member_coupon (
    issue_id         INT AUTO_INCREMENT COMMENT '발급ID',
    coupon_id        INT NOT NULL COMMENT '쿠폰ID',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '회원 쿠폰 발급내역';


-- Source: manager.sql
-- -----------------------------------------------------
-- Manager (Admin)
-- -----------------------------------------------------
CREATE TABLE manager (
    manager_seq      INT AUTO_INCREMENT COMMENT '관리자SEQ',
    manager_code     VARCHAR(30) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    manager_id       VARCHAR(20) NOT NULL COMMENT '아이디',
    password         VARCHAR(200) NOT NULL COMMENT '비밀번호',
    login_fail_count INT DEFAULT 0 NULL COMMENT '로그인실패횟수',
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    manager_name     VARCHAR(50) NOT NULL COMMENT '이름',
    manager_email    VARCHAR(50) NOT NULL COMMENT '이메일',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    apply_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '승인여부',
    description      VARCHAR(200) NULL COMMENT '설명',
    memo             VARCHAR(2000) NULL COMMENT '메모',
    login_date       DATETIME NULL COMMENT '최근로그인일시',
    mb_type          VARCHAR(20) DEFAULT '' NOT NULL COMMENT '관리자유형(MASTER, SCM, ADMIN)',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (manager_seq),
    UNIQUE KEY uq_manager_id (manager_id),
    UNIQUE KEY uq_manager_code (manager_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '관리자 정보';

-- -----------------------------------------------------
-- Manager Menu
-- -----------------------------------------------------
CREATE TABLE manager_menu (
    menu_seq         INT AUTO_INCREMENT COMMENT '메뉴SEQ',
    parent_menu_seq  INT NULL COMMENT '상위메뉴SEQ',
    menu_name        VARCHAR(100) NOT NULL COMMENT '메뉴명',
    menu_url         VARCHAR(100) NULL COMMENT '메뉴URL',
    display_yn       CHAR(1) NOT NULL COMMENT '노출여부',
    display_order    INT NULL COMMENT '표시순서',
    menu_parameter   VARCHAR(100) DEFAULT '' NOT NULL COMMENT '파라미터',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (menu_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '관리자 메뉴';

-- -----------------------------------------------------
-- Manager Auth Group
-- -----------------------------------------------------
CREATE TABLE manager_auth_group (
    auth_group_seq   INT AUTO_INCREMENT COMMENT '권한그룹SEQ',
    auth_group_name  VARCHAR(100) NOT NULL COMMENT '권한그룹명',
    use_yn           CHAR(1) NOT NULL COMMENT '사용여부',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (auth_group_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '관리자 권한 그룹';

-- -----------------------------------------------------
-- Manager Menu Group Mapping
-- -----------------------------------------------------
CREATE TABLE manager_menu_group (
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    menu_seq         INT NOT NULL COMMENT '메뉴SEQ',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    PRIMARY KEY (auth_group_seq, menu_seq),
    CONSTRAINT fk_mmg_auth FOREIGN KEY (auth_group_seq) REFERENCES manager_auth_group (auth_group_seq) ON DELETE CASCADE,
    CONSTRAINT fk_mmg_menu FOREIGN KEY (menu_seq) REFERENCES manager_menu (menu_seq) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '관리자 메뉴 권한 매핑';

-- -----------------------------------------------------
-- Manager SCM Information
-- -----------------------------------------------------

CREATE TABLE manager_scm (
    manager_seq           INT NOT NULL COMMENT '관리자SEQ(FK)',
    manager_code          VARCHAR(20) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    supplier_name         VARCHAR(50) NOT NULL COMMENT '공급사명',
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
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    created_by       VARCHAR(50) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    CONSTRAINT PK_MANAGER_SCM PRIMARY KEY (manager_seq),
    CONSTRAINT FK_MANAGER_SCM_BASE FOREIGN KEY (manager_seq) REFERENCES manager (manager_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SCM 관리자 상세정보';


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '기업회원 상세정보';


-- Source: order.sql
-- 1. Cart (Shopping Cart)
CREATE TABLE cart (
    cart_id          INT AUTO_INCREMENT COMMENT '장바구니ID',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    quantity         INT DEFAULT 1 NOT NULL COMMENT '수량',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (cart_id),
    INDEX idx_cart_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '장바구니';

-- 2. Order Master
CREATE TABLE order_master (
    order_id         INT AUTO_INCREMENT COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호(UUID)',
    order_name       VARCHAR(200) NULL COMMENT '주문명',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    order_status     VARCHAR(20) DEFAULT 'PAYMENT_WAIT' NOT NULL COMMENT '주문상태(PAYMENT_WAIT:결제대기, PREPARING:배송준비, SHIPPING:배송중, DELIVERED:배송완료, CANCELLED:주문취소, RETURN_REQUEST:반품요청, EXCHANGE_REQUEST:교환요청)',
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
    memo              TEXT NULL COMMENT '관리자메모',
    -- BaseEntity Information
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시(주문일시)',
    created_by       VARCHAR(50) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (order_id),
    UNIQUE KEY uq_order_no (order_no),
    INDEX idx_order_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '주문 마스터';

-- 3. Order Detail (Line Items)
CREATE TABLE order_detail (
    order_detail_id  INT AUTO_INCREMENT COMMENT '주문상세ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호',
    order_seq        INT NOT NULL COMMENT '주문순번',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    order_status     VARCHAR(20) DEFAULT 'PAYMENT_WAIT' NOT NULL COMMENT '주문상태(PAYMENT_WAIT:결제대기, PREPARING:배송준비, SHIPPING:배송중, DELIVERED:배송완료, CANCELLED:주문취소, RETURN_REQUEST:반품요청, EXCHANGE_REQUEST:교환요청)',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명(스냅샷)',
    option_name      VARCHAR(100) NULL COMMENT '옵션명(스냅샷)',
    product_price    DECIMAL(19,4) NOT NULL COMMENT '상품가격',
    option_price     DECIMAL(19,4) DEFAULT 0 COMMENT '옵션가격',
    quantity         INT NOT NULL COMMENT '주문수량',
    total_price      DECIMAL(19,4) NOT NULL COMMENT '총금액(구 sub_total)',
    point_amount     INT DEFAULT 0 NOT NULL COMMENT '적립포인트',
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
    created_by       VARCHAR(50) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (order_detail_id),
    CONSTRAINT fk_detail_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '주문 상세';


-- Source: delivery.sql
-- 1. Delivery
CREATE TABLE delivery (
    delivery_id      INT AUTO_INCREMENT COMMENT '배송ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호',
    site_cd          VARCHAR(20) NOT NULL COMMENT '사이트코드',
    order_detail_id  INT NOT NULL COMMENT '주문상세ID',
    delivery_corp    VARCHAR(200) NULL COMMENT '택배사명',
    tracking_number  VARCHAR(50) NULL COMMENT '운송장번호',
    status           VARCHAR(20) DEFAULT 'READY' NOT NULL COMMENT '배송상태(READY, SHIPPING, COMPLETE)',
    shipped_at       DATETIME NULL COMMENT '출고일시',
    completed_at     DATETIME NULL COMMENT '배송완료일시',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(50) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (delivery_id),
    UNIQUE KEY uq_delivery_order (order_id),
    FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '배송 정보';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '배송지 목록';


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '주문 클레임';


-- Source: payment.sql
CREATE TABLE payment (
    payment_id             INT(11) NOT NULL AUTO_INCREMENT COMMENT '결제코드',
    order_id               INT(11) NOT NULL COMMENT '주문ID',
    site_cd                VARCHAR(20) NOT NULL COMMENT '사이트 코드',
    order_no               VARCHAR(50) NOT NULL COMMENT '주문 번호',
    member_code            VARCHAR(30) NOT NULL COMMENT '회원코드',
    total_price            DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '총주문금액',
    discount_price         DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '총할인금액',
    used_point             DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '포인트사용액',
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
    cancel_point_price     DECIMAL(19,4) DEFAULT 0.0000 COMMENT '취소포인트액',
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


-- 2. 현금영수증 내역 테이블
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


-- 3. 세금계산서 내역 테이블
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



-- Source: point.sql
CREATE TABLE point (
  point_id int(11) NOT NULL AUTO_INCREMENT COMMENT '포인트코드',
  site_cd varchar(20) DEFAULT NULL COMMENT '사이트코드',
  point_use int(11) NOT NULL COMMENT '사용/적립 포인트',
  point_bigo varchar(255) DEFAULT NULL COMMENT '포인트 상세 이력',
  point_type varchar(20) NOT NULL COMMENT '포인트 구분(SAVE:적립, USE:사용)',
  member_code varchar(30) NOT NULL COMMENT '회원코드',
  order_no varchar(50) DEFAULT NULL COMMENT '주문번호',
  created_at datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일',
  created_by varchar(50) DEFAULT NULL COMMENT '생성자',
  updated_at datetime DEFAULT NULL COMMENT '수정일',
  updated_by varchar(50) DEFAULT NULL COMMENT '수정자',
  deleted_at datetime DEFAULT NULL COMMENT '삭제일',
  deleted_by varchar(50) DEFAULT NULL COMMENT '삭제자',
  delete_yn char(1) NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (point_id),
  KEY fk_point_member (member_code),
  CONSTRAINT fk_point_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='포인트';



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
    is_secret    CHAR(1) NOT NULL DEFAULT 'N' COMMENT '비밀글 여부',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by   VARCHAR(50) NULL COMMENT '생성자',
    updated_at   DATETIME NULL COMMENT '수정일',
    updated_by   VARCHAR(50) NULL COMMENT '수정자',
    deleted_at   DATETIME NULL COMMENT '삭제일',
    deleted_by   VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn    CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (inquiry_id),
    INDEX idx_inquiry_status (status),
    CONSTRAINT fk_inquiry_writer FOREIGN KEY (writer_code) REFERENCES member (member_code) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '문의';



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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '기업전용상품매핑';

-- Source: wishlist.sql
-- -----------------------------------------------------
-- Wishlist (Product Wishlist)
-- -----------------------------------------------------
CREATE TABLE wishlist (
  wishlist_id int(11) NOT NULL AUTO_INCREMENT COMMENT '찜ID',
  site_cd varchar(20) DEFAULT NULL COMMENT '사이트코드',
  member_code varchar(30) NOT NULL COMMENT '회원코드(FK)',
  product_id int(11) NOT NULL COMMENT '상품ID(FK)',
  created_at datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일시',
  created_by varchar(50) DEFAULT NULL COMMENT '등록자',
  updated_at datetime DEFAULT NULL COMMENT '수정일',
  updated_by varchar(50) DEFAULT NULL COMMENT '수정자',
  deleted_at datetime DEFAULT NULL COMMENT '삭제일',
  deleted_by varchar(50) DEFAULT NULL COMMENT '삭제자',
  delete_yn char(1) NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (wishlist_id),
  UNIQUE KEY uq_wishlist_user_prod (member_code,product_id),
  KEY fk_wishlist_product (product_id),
  CONSTRAINT fk_wishlist_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
  CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 찜 목록';


-- Source: site_policy.sql
-- 🏗️ 사이트 정책 설정 테이블 (단일 행 UPDATE 방식 관리)
CREATE TABLE site_policy (
    seq               INT AUTO_INCREMENT COMMENT '시퀀스',
    site_cd           VARCHAR(20) NOT NULL COMMENT '사이트코드',
    
    terms_of_use      MEDIUMTEXT NULL COMMENT '이용약관',
    privacy_policy    MEDIUMTEXT NULL COMMENT '개인정보처리방침',
    legal_notice      MEDIUMTEXT NULL COMMENT '법적고지',
    marketing_consent MEDIUMTEXT NULL COMMENT '마케팅활용동의',
    footer_info       TEXT NULL COMMENT '푸터 정보',

    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by        VARCHAR(50) NULL COMMENT '생성자',
    updated_at        DATETIME NULL COMMENT '수정일',
    updated_by        VARCHAR(50) NULL COMMENT '수정자',
    deleted_at        DATETIME NULL COMMENT '삭제일',
    deleted_by        VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (seq),
    UNIQUE KEY uk_site_policy_site_cd (site_cd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '사이트 정책 설정';

-- Source: inout.sql
CREATE TABLE inout_master (
  io_seq 			INT(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  io_code 			VARCHAR(45) NOT NULL COMMENT '입출고코드',
  io_type 			VARCHAR(10) NOT NULL COMMENT '입출고구분 IN: 입고, OUT: 출고',
  io_category 		VARCHAR(20) NOT NULL COMMENT '입출고 상세카테고리 (일반입고, 생산입고, 반품입고, 일반출고, 생산출고, 주문출고, 폐기출고)',
  io_date 			DATETIME DEFAULT NULL COMMENT '입고일',
  order_no 			VARCHAR(45) DEFAULT NULL COMMENT '주문번호',
  manager_code		VARCHAR(30) DEFAULT NULL COMMENT '관리자코드(MGR+6자리)',

  created_at 		DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  created_by 		VARCHAR(50) NULL COMMENT '생성자',
  updated_at 		DATETIME NULL COMMENT '수정일',
  updated_by 		VARCHAR(50) NULL COMMENT '수정자',
  deleted_at 		DATETIME NULL COMMENT '삭제일',
  deleted_by 		VARCHAR(50) NULL COMMENT '삭제자',
  delete_yn 		CHAR(1) NOT NULL DEFAULT 'N' COMMENT '삭제유무',
  PRIMARY KEY (io_seq),
  UNIQUE KEY uq_inout_io_code (io_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='입출고 Master';


CREATE TABLE inout_detail (
  io_code 			VARCHAR(45) NOT NULL COMMENT '입출고코드 (IO+8자리)',
  no 				INT(11) NOT NULL COMMENT '번호',
  io_type 			VARCHAR(10) NOT NULL COMMENT '입출고구분 IN: 입고, OUT: 출고',
  in_io_code 		VARCHAR(45) DEFAULT NULL COMMENT '입고 코드',
  in_no	 	 		INT(11) DEFAULT NULL COMMENT '입고 코드의번호', 
  product_id 		INT(11) DEFAULT NULL COMMENT '상품ID',
  option_id 		INT(11) DEFAULT NULL COMMENT '옵션ID',
  product_name 		VARCHAR(200) DEFAULT NULL COMMENT '상품명',
  option_name 		VARCHAR(200) DEFAULT NULL COMMENT '옵션명',
  brand_name 		VARCHAR(100) DEFAULT NULL COMMENT '브랜드명',
  qty 				INT(11) DEFAULT '0' COMMENT '입출고수량',
  real_qty 			INT(11) DEFAULT '0' COMMENT '재고잔량',
  from_type 		CHAR(1) DEFAULT NULL COMMENT 'FROM (C:거래처, S:창고, L:라인)',
  location_type 	CHAR(1) DEFAULT NULL COMMENT 'TO (C:거래처, S:창고, L:라인)',
  to_type 			CHAR(1) DEFAULT NULL COMMENT 'TO (C:거래처, S:창고, L:라인)',
  location1 		INT(11) DEFAULT NULL COMMENT '위치1',
  location2 		INT(11) DEFAULT NULL COMMENT '위치2',
  location3 		INT(11) DEFAULT NULL COMMENT '위치3',
  from_location1 	INT(11) DEFAULT NULL COMMENT 'FROM 위치1',
  from_location2 	INT(11) DEFAULT NULL COMMENT 'FROM 위치2',
  from_location3 	INT(11) DEFAULT NULL COMMENT 'FROM 위치3',
  to_location1 		INT(11) DEFAULT NULL COMMENT 'TO 위치1',
  to_location2 		INT(11) DEFAULT NULL COMMENT 'TO 위치2',
  to_location3 		INT(11) DEFAULT NULL COMMENT 'TO 위치3',
  memo 				VARCHAR(200) DEFAULT NULL COMMENT '메모',

  created_at 		DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  created_by 		VARCHAR(50) NULL COMMENT '생성자',
  updated_at 		DATETIME NULL COMMENT '수정일',
  updated_by 		VARCHAR(50) NULL COMMENT '수정자',
  deleted_at 		DATETIME NULL COMMENT '삭제일',
  deleted_by 		VARCHAR(50) NULL COMMENT '삭제자',
  delete_yn 		CHAR(1) NOT NULL DEFAULT 'N' COMMENT '삭제유무',
  PRIMARY KEY (io_code, no),
  CONSTRAINT fk_inout_detail_io_code FOREIGN KEY (io_code) REFERENCES inout_master (io_code) ON DELETE CASCADE,
  CONSTRAINT fk_inout_detail_product FOREIGN KEY (product_id) REFERENCES product (product_id),
  KEY idx_inout_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='입출고 Detail';


-- source: shipment.sql
CREATE TABLE shipment (
    shipment_id INT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    shipment_code VARCHAR(30) NOT NULL COMMENT '출고지 코드(SHIP + 숫자 6자리)',
    shipment_type VARCHAR(20) NOT NULL DEFAULT 'OUT' COMMENT 'IN(입고) / OUT(출고)',
    shipment_name VARCHAR(100) NOT NULL COMMENT '출고지명',
    zipcode VARCHAR(10) NOT NULL COMMENT '우편번호',
    address VARCHAR(200) NOT NULL COMMENT '주소',
    address_detail VARCHAR(200) NOT NULL COMMENT '상세주소',
    supplier_name VARCHAR(50) NOT NULL COMMENT '공급자명',
    phone VARCHAR(45) NOT NULL COMMENT '전화번호',
    mobile VARCHAR(45) DEFAULT NULL COMMENT '휴대전화번호',
    shipping_fee DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '기본 배송비',
    return_fee DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '반품 배송비(편도)',
    exchange_fee DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '교환 배송비(왕복)',
    delivery_island_yn	CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '제주/도서산간 배송여부',
    delivery_island_fee	DECIMAL(19,4) NOT NULL DEFAULT 0.0000 COMMENT '제주/도서산간 추가배송비',
    is_default CHAR(1) NOT NULL DEFAULT 'N' COMMENT '기본 배송지 여부 (Y/N)',
    
    created_at 		DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    created_by 		VARCHAR(50) NULL COMMENT '생성자',
    updated_at 		DATETIME NULL COMMENT '수정일',
    updated_by 		VARCHAR(50) NULL COMMENT '수정자',
    deleted_at 		DATETIME NULL COMMENT '삭제일',
    deleted_by 		VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn 		CHAR(1) NOT NULL DEFAULT 'N' COMMENT '삭제유무',
    PRIMARY KEY (shipment_id),UNIQUE KEY uq_shipment_code (shipment_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='출고지/입고지 및 배송 정책';

