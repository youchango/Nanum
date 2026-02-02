CREATE TABLE basic (
    basic_id   INT AUTO_INCREMENT COMMENT '기본설정코드',
    type       VARCHAR(20) NOT NULL COMMENT '타입',
    content    VARCHAR(4000) NULL COMMENT '컨텐츠',
    updated_at DATETIME NULL COMMENT '수정일',
    updated_by INT NULL COMMENT '수정자',
    PRIMARY KEY (basic_id)
) COMMENT '기본설정';
