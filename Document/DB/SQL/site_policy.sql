-- 🏗️ 사이트 정책 설정 테이블 (단일 행 UPDATE 방식 관리)
CREATE TABLE site_policy (
    seq               INT AUTO_INCREMENT COMMENT '시퀀스',
    site_cd           VARCHAR(20) NOT NULL COMMENT '사이트코드',
    terms_of_use      MEDIUMTEXT NULL COMMENT '이용약관',
    privacy_policy    MEDIUMTEXT NULL COMMENT '개인정보처리방침',
    legal_notice      MEDIUMTEXT NULL COMMENT '법적고지',
    marketing_consent MEDIUMTEXT NULL COMMENT '마케팅활용동의',
    footer_info       TEXT NULL COMMENT '푸터 정보',
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    created_by        VARCHAR(50) NULL COMMENT '생성자',
    updated_at        DATETIME NULL COMMENT '수정일',
    updated_by        VARCHAR(50) NULL COMMENT '수정자',
    deleted_at        DATETIME NULL COMMENT '삭제일',
    deleted_by        VARCHAR(50) NULL COMMENT '삭제자',
    delete_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제유무',
    PRIMARY KEY (seq),
    UNIQUE KEY uk_site_policy_site_cd (site_cd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '사이트 정책 설정';

-- 초기 정책 데이터 세팅 (이후 백엔드에서는 UPDATE만 수행)
INSERT INTO site_policy (site_cd, terms_of_use, privacy_policy, legal_notice, marketing_consent, footer_info, created_by)
VALUES ('DEFAULT', NULL, NULL, NULL, NULL, NULL, 'SYSTEM');

