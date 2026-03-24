CREATE TABLE file_store (
    file_id        VARCHAR(36) NOT NULL COMMENT '파일ID (UUID)',
    reference_type VARCHAR(50) NOT NULL COMMENT '참조구분 (PRODUCT, CATEGORY, BANNER, POPUP, REVIEW, INQUIRY)',
    reference_id   VARCHAR(50) NOT NULL COMMENT '참조ID (Entity PK)',
    org_name       VARCHAR(255) NOT NULL COMMENT '원본파일명',
    save_name      VARCHAR(255) NOT NULL COMMENT '저장파일명(UUID포함)',
    path           VARCHAR(500) NOT NULL COMMENT '전체 URL 또는 상대경로',
    ext            VARCHAR(10) NOT NULL COMMENT '확장자',
    size           BIGINT DEFAULT 0 NOT NULL COMMENT '파일크기(Byte)',
    
    is_main        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '대표이미지 여부(Y/N)',
    display_order  INT DEFAULT 0 NOT NULL COMMENT '노출순서',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(50) NOT NULL COMMENT '등록자',
    updated_at       DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    updated_by       VARCHAR(50) DEFAULT NULL COMMENT '수정자',
    deleted_at       DATETIME DEFAULT NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(50) DEFAULT NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부(Y/N)',
    
    PRIMARY KEY (file_id),
    INDEX idx_file_ref (reference_type, reference_id),
    INDEX idx_file_reg (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='전사 통합 파일 관리';
