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
