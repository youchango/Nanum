CREATE TABLE point (
    point_id    INT AUTO_INCREMENT COMMENT '포인트코드',
    site_cd     VARCHAR(20) NULL COMMENT '사이트코드',
    point_use   INT NOT NULL COMMENT '사용/적립 포인트',
    point_bigo  VARCHAR(255) NULL COMMENT '포인트 상세 이력',
    point_gubun VARCHAR(20) NOT NULL COMMENT '구분(SAVE:적립, USE:사용)',
    member_code VARCHAR(30) NOT NULL COMMENT '회원코드',
    order_no    VARCHAR(50) NULL COMMENT '주문번호',
    created_by  INT NULL COMMENT '등록자',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일',
    
    PRIMARY KEY (point_id),
    CONSTRAINT fk_point_member FOREIGN KEY (member_code) REFERENCES member (member_code) ON DELETE CASCADE
) COMMENT '포인트';
