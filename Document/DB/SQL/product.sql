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
) COMMENT '상품 마스터';

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
) COMMENT '상품 사이트별 정보';

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
) COMMENT '상품 재고(창고)';
