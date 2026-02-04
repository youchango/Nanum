-- 1. Cart (Shopping Cart)
CREATE TABLE cart (
    cart_id          INT AUTO_INCREMENT COMMENT '장바구니ID',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    quantity         INT DEFAULT 1 NOT NULL COMMENT '수량',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    updated_at       DATETIME NULL COMMENT '수정일',
    
    PRIMARY KEY (cart_id),
    INDEX idx_cart_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE
) COMMENT '장바구니';

-- 2. Order Master
CREATE TABLE order_master (
    order_id         INT AUTO_INCREMENT COMMENT '주문ID',
    order_no         VARCHAR(50) NOT NULL COMMENT '주문번호(UUID)',
    member_code      VARCHAR(30) NOT NULL COMMENT '회원코드',
    order_status     VARCHAR(20) DEFAULT 'PAY_WAIT' NOT NULL COMMENT '주문상태(PAY_WAIT, PAID, PREPARE, DELIVERY, COMPLETE, CANCEL, REFUND)',
    
    -- Amount Info
    total_amount     INT DEFAULT 0 NOT NULL COMMENT '총주문금액',
    discount_amount  INT DEFAULT 0 NOT NULL COMMENT '할인금액',
    delivery_fee     INT DEFAULT 0 NOT NULL COMMENT '배송비',
    payment_amount   INT DEFAULT 0 NOT NULL COMMENT '실결제금액',
    
    -- Receiver Info
    receiver_name    VARCHAR(50) NOT NULL COMMENT '수령자명',
    receiver_phone   VARCHAR(20) NOT NULL COMMENT '수령자연락처',
    receiver_zipcode  VARCHAR(10) NOT NULL COMMENT '우편번호',
    receiver_address  VARCHAR(255) NOT NULL COMMENT '주소',
    receiver_detail   VARCHAR(255) NOT NULL COMMENT '상세주소',
    delivery_memo     VARCHAR(200) NULL COMMENT '배송메모',
    tracking_number   VARCHAR(50) NULL COMMENT '운송장번호',
    
    ordered_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '주문일시',
    updated_at       DATETIME NULL COMMENT '수정일시',
    
    PRIMARY KEY (order_id),
    UNIQUE KEY uq_order_no (order_no),
    INDEX idx_order_member (member_code),
    FOREIGN KEY (member_code) REFERENCES member (member_code)
) COMMENT '주문 마스터';

-- 3. Order Detail (Line Items)
CREATE TABLE order_detail (
    order_detail_id  INT AUTO_INCREMENT COMMENT '주문상세ID',
    order_id         INT NOT NULL COMMENT '주문ID',
    product_id       INT NOT NULL COMMENT '상품ID',
    option_id        INT NULL COMMENT '옵션ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '상품명(스냅샷)',
    option_name      VARCHAR(100) NULL COMMENT '옵션명(스냅샷)',
    price            INT NOT NULL COMMENT '판매가(스냅샷)',
    discount_price   INT DEFAULT 0 NOT NULL COMMENT '할인금액',
    quantity         INT NOT NULL COMMENT '주문수량',
    sub_total        INT NOT NULL COMMENT '소계(가격*수량)',
    detail_status    VARCHAR(20) NULL COMMENT '개별상태(필요시 사용)',
    PRIMARY KEY (order_detail_id),
    CONSTRAINT fk_detail_order FOREIGN KEY (order_id) REFERENCES order_master (order_id) ON DELETE CASCADE
) COMMENT '주문 상세';
