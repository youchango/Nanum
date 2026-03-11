-- -----------------------------------------------------
-- Wishlist (Product Wishlist)
-- -----------------------------------------------------
CREATE TABLE wishlist (
    wishlist_id      INT AUTO_INCREMENT COMMENT '찜ID',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드(FK)',
    product_id       INT NOT NULL COMMENT '상품ID(FK)',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NULL COMMENT '등록자',
    updated_at       DATETIME NULL DEFAULT NULL COMMENT '수정일',
    updated_by       VARCHAR(50) NULL COMMENT '수정자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (wishlist_id),
    UNIQUE KEY uq_wishlist_user_prod (member_code, product_id),
    CONSTRAINT fk_wishlist_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '상품 찜 목록';
