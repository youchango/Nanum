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
    thumbnail_url    VARCHAR(500) NULL COMMENT '대표이미지URL',
    view_count       INT DEFAULT 0 NOT NULL COMMENT '조회수',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    updated_at       DATETIME NULL COMMENT '수정일',
    deleted_at       DATETIME NULL COMMENT '삭제일',
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

-- 4. Product Image
CREATE TABLE product_image (
    image_id         INT AUTO_INCREMENT COMMENT '이미지ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    image_url        VARCHAR(500) NOT NULL COMMENT '이미지경로',
    image_type       VARCHAR(20) DEFAULT 'DETAIL' NOT NULL COMMENT '타입(MAIN, DETAIL)',
    display_order    INT DEFAULT 0 NOT NULL COMMENT '노출순서',
    
    PRIMARY KEY (image_id),
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 이미지';

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
