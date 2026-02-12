-- 1. Delivery
CREATE TABLE delivery (
    delivery_id      INT AUTO_INCREMENT COMMENT '배송ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호',
    order_detail_id  INT NOT NULL COMMENT '주문상세ID',
    delivery_corp    VARCHAR(200) NULL COMMENT '택배사명',
    tracking_number  VARCHAR(50) NULL COMMENT '운송장번호',
    status           VARCHAR(20) DEFAULT 'READY' NOT NULL COMMENT '배송상태(READY, SHIPPING, COMPLETE)',
    
    shipped_at       DATETIME NULL COMMENT '출고일시',
    completed_at     DATETIME NULL COMMENT '배송완료일시',

    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    created_by       VARCHAR(20) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일',
    updated_by       VARCHAR(20) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일',
    deleted_by       VARCHAR(20) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    
    PRIMARY KEY (delivery_id),
    UNIQUE KEY uq_delivery_order (order_id),
    FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '배송 정보';

-- 2. Address Book
CREATE TABLE address_book (
    address_id       INT AUTO_INCREMENT COMMENT '배송지ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
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
    INDEX idx_addr_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '배송지 목록';
