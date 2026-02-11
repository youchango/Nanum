CREATE TABLE site_policy_history (
    seq            INT AUTO_INCREMENT COMMENT '시퀀스',
    site_cd        VARCHAR(20) NOT NULL COMMENT '사이트코드',
    
    terms_of_use   MEDIUMTEXT NULL COMMENT '이용약관',
    privacy_policy MEDIUMTEXT NULL COMMENT '개인정보처리방침',
    legal_notice   MEDIUMTEXT NULL COMMENT '법적고지',
    footer_info    TEXT NULL COMMENT '푸터 노출 정보',
    
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by     INT NULL COMMENT '생성자',
    updated_at     DATETIME NULL COMMENT '수정일',
    updated_by     INT NULL COMMENT '수정자',
    delete_yn      CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    
    PRIMARY KEY (seq)
) COMMENT '사이트별 정책 이력 관리';
