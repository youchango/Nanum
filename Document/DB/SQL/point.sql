CREATE TABLE point (
    point_id   INT AUTO_INCREMENT COMMENT '포인트코드',
    point_use  INT NOT NULL COMMENT '사용/적립 포인트',
    point_bigo VARCHAR(255) NULL COMMENT '포인트 상세 이력',
    member_id  INT NOT NULL COMMENT '멤버코드',
    created_by INT NULL COMMENT '등록자',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    payment_id INT NULL COMMENT '결제코드',
    PRIMARY KEY (point_id),
    CONSTRAINT fk_point_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_point_payment FOREIGN KEY (payment_id) REFERENCES payment_master (payment_id) ON DELETE SET NULL
) COMMENT '포인트';
