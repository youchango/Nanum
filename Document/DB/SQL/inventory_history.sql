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
