CREATE TABLE content (
    content_id   INT AUTO_INCREMENT COMMENT '컨텐츠 코드',
    site_cd      VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    content_type INT NOT NULL COMMENT '구분(Code ID)',
    subject      VARCHAR(200) NOT NULL COMMENT '제목',
    content_body LONGTEXT NOT NULL COMMENT '내용',
    url_info     VARCHAR(255) NULL COMMENT 'URL 정보',
    updated_by   BIGINT NULL COMMENT '수정자',
    updated_at   DATETIME NULL COMMENT '수정일',
    created_by   BIGINT NULL COMMENT '생성자',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    deleted_yn   CHAR(1) DEFAULT 'N' COMMENT '삭제 여부',
    deleted_at   DATETIME NULL COMMENT '삭제일',
    deleted_by   BIGINT NULL COMMENT '삭제자',
    PRIMARY KEY (content_id)
) COMMENT '컨텐츠';
