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