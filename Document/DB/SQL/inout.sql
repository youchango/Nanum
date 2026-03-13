CREATE TABLE `inout_master` (
  `io_seq` 			INT(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `io_code` 		VARCHAR(45) DEFAULT NULL COMMENT '입출고코드',
  `io_type` 		VARCHAR(10) NOT NULL COMMENT '입출고구분',
  `io_date` 		DATETIME DEFAULT NULL COMMENT '입고일',
  `order_no` 		VARCHAR(45) DEFAULT NULL COMMENT '주문번호',
  `manager_code`	VARCHAR(30) DEFAULT NULL COMMENT '관리자코드(MGR+6자리)',

  `created_at` 		DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `created_by` 		INT NULL COMMENT '생성자',
  `updated_at` 		DATETIME NULL COMMENT '수정일',
  `updated_by` 		INT NULL COMMENT '수정자',
  `deleted_at` 		DATETIME NULL COMMENT '삭제일',
  `deleted_by` 		VARCHAR(50) NULL COMMENT '삭제자',
  `delete_yn` 		CHAR(1) NOT NULL DEFAULT 'N' COMMENT '삭제유무',
  PRIMARY KEY (`io_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='입출고 Master';


CREATE TABLE `inout_detail` (
  `io_code` 		VARCHAR(45) NOT NULL,
  `io_code2` 		VARCHAR(45) DEFAULT NULL,
  `io_code_no2` 	int(11) DEFAULT NULL,
  `io_type` 		VARCHAR(10) NOT NULL COMMENT '입출고구분',
  `no` 				int(11) DEFAULT NULL COMMENT '번호',
  `product_cd` 		int(11) DEFAULT NULL COMMENT '상품id',
  `product_name` 	VARCHAR(500) DEFAULT NULL COMMENT '상품명',
  `good_qty` 		decimal(15,4) DEFAULT '0.0000' COMMENT '양품수량',
  `real_good_qty` 	decimal(15,4) DEFAULT '0.0000',
  `bad_qty` 		decimal(15,4) DEFAULT '0.0000' COMMENT '불량수량',
  `bad_reason` 		VARCHAR(10) DEFAULT NULL COMMENT '불량사유',
  `from_type` 		VARCHAR(1) DEFAULT NULL COMMENT 'FROM (C거래처S창고L라인)',
  `location_type` 	VARCHAR(1) DEFAULT NULL COMMENT 'TO(C거래처S창고L라인)',
  `to_type` 		VARCHAR(1) DEFAULT NULL COMMENT 'TO(C거래처S창고L라인)',
  `location1` 		int(11) DEFAULT NULL,
  `location2` 		int(11) DEFAULT NULL,
  `location3` 		int(11) DEFAULT NULL,
  `from_location1` 	int(11) DEFAULT NULL,
  `from_location2` 	int(11) DEFAULT NULL,
  `from_location3` 	int(11) DEFAULT NULL,
  `to_location1` 	int(11) DEFAULT NULL,
  `to_location2` 	int(11) DEFAULT NULL,
  `to_location3` 	int(11) DEFAULT NULL,
  `memo` 			VARCHAR(100) DEFAULT NULL COMMENT '메모',
  `delete_yn` 		VARCHAR(10) NOT NULL DEFAULT 'N',
  `delete_date` 	datetime DEFAULT NULL,
  `delete_by` 		VARCHAR(10) DEFAULT NULL,
  KEY `idx_md_key_mes` (`pdt_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='입출고 Detail';