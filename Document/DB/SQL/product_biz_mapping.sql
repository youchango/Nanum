-- [NEW] Product Corporate Mapping (Exclusive Products)
-- 특정 기업회원에게만 노출되거나 혜택이 적용되는 상품을 관리합니다.
CREATE TABLE product_biz_mapping (
    mapping_id       INT AUTO_INCREMENT COMMENT '매핑ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    member_id        INT NOT NULL COMMENT '회원ID (기업회원)',
    discount_rate    INT DEFAULT 0 NOT NULL COMMENT '해당 기업 전용 할인율(%)',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       INT NULL COMMENT '등록자',
    
    PRIMARY KEY (mapping_id),
    UNIQUE KEY uq_prod_biz (product_id, member_id),
    CONSTRAINT fk_mapping_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_mapping_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
) COMMENT '기업 전용 상품 매핑';
