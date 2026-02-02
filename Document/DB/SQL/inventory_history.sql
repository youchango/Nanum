-- [NEW] Inventory History (Product Stock In/Out)
-- 상품 및 옵션별 입출고 이력을 기록하여 재고 흐름을 추적합니다.
CREATE TABLE inventory_history (
    history_id       INT AUTO_INCREMENT COMMENT '이력ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    type             VARCHAR(20) NOT NULL COMMENT '구분(IN:입고, OUT:출고, RETURN:반품입고, ADJUST:재고조정)',
    quantity         INT NOT NULL COMMENT '수량(항상 양수)',
    before_quantity  INT NOT NULL COMMENT '변동전재고',
    after_quantity   INT NOT NULL COMMENT '변동후재고',
    order_id         INT NULL COMMENT '연관주문ID(출고/반품시)',
    memo             VARCHAR(500) NULL COMMENT '비고 및 사유',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       INT NULL COMMENT '등록자(관리자ID)',
    
    PRIMARY KEY (history_id),
    INDEX idx_inv_product (product_id),
    INDEX idx_inv_type (type),
    CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '재고 입출고 이력';
