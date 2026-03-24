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