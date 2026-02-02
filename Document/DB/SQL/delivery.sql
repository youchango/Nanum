-- 1. Delivery
CREATE TABLE delivery (
    delivery_id      INT AUTO_INCREMENT COMMENT '배송ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    courier_company  VARCHAR(20) NULL COMMENT '택배사코드',
    tracking_number  VARCHAR(50) NULL COMMENT '운송장번호',
    status           VARCHAR(20) DEFAULT 'READY' NOT NULL COMMENT '배송상태(READY, SHIPPING, COMPLETE)',
    
    shipped_at       DATETIME NULL COMMENT '출고일시',
    completed_at     DATETIME NULL COMMENT '배송완료일시',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    updated_at       DATETIME NULL COMMENT '수정일',
    
    PRIMARY KEY (delivery_id),
    UNIQUE KEY uq_delivery_order (order_id)
) COMMENT '배송 정보';

-- 2. Address Book
CREATE TABLE address_book (
    address_id       INT AUTO_INCREMENT COMMENT '배송지ID',
    member_id        INT NOT NULL COMMENT '회원ID',
    address_name     VARCHAR(50) NULL COMMENT '배송지명(우리집, 회사 등)',
    dept_name        VARCHAR(100) NULL COMMENT '부서/프로젝트명 (기업회원용)',
    receiver_name    VARCHAR(50) NOT NULL COMMENT '수령자명',
    receiver_phone   VARCHAR(20) NOT NULL COMMENT '수령자연락처',
    zipcode          VARCHAR(10) NOT NULL COMMENT '우편번호',
    address          VARCHAR(255) NOT NULL COMMENT '주소',
    address_detail   VARCHAR(255) NOT NULL COMMENT '상세주소',
    is_default       CHAR(1) DEFAULT 'N' NOT NULL COMMENT '기본배송지여부',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    
    PRIMARY KEY (address_id),
    INDEX idx_addr_member (member_id)
) COMMENT '배송지 목록';
