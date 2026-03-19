CREATE TABLE code (
    code_id    INT AUTO_INCREMENT COMMENT '코드관리 PK',
    code_type  VARCHAR(20) NOT NULL COMMENT '코드타입',
    depth      INT NULL COMMENT 'depth',
    upper      INT NULL COMMENT 'upper',
    code_name  VARCHAR(100) NOT NULL COMMENT '코드명',
    use_yn     CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용유무',
    delete_yn  CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by VARCHAR(50) NULL COMMENT '생성자',
    updated_at DATETIME NULL COMMENT '수정일',
    updated_by VARCHAR(50) NULL COMMENT '수정자',
    deleted_at DATETIME NULL COMMENT '삭제일',
    deleted_by VARCHAR(50) NULL COMMENT '삭제자',
    PRIMARY KEY (code_id),
    INDEX idx_code_type (code_type),
    CONSTRAINT fk_code_upper FOREIGN KEY (upper) REFERENCES code (code_id) ON DELETE SET NULL
) COMMENT '코드관리';
