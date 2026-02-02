-- 8.1 파일 저장 관리 (file_store)
CREATE TABLE `file_store` (
    `file_id` VARCHAR(36) NOT NULL COMMENT '파일ID (UUID)',
    `group_id` VARCHAR(36) NOT NULL COMMENT '파일그룹ID (UUID)',
    `org_name` VARCHAR(255) NOT NULL COMMENT '원본파일명',
    `save_name` VARCHAR(255) NOT NULL COMMENT '저장파일명',
    `size` BIGINT NOT NULL COMMENT '파일크기',
    `ext` VARCHAR(10) NOT NULL COMMENT '확장자',
    `path` VARCHAR(255) NOT NULL COMMENT '저장경로',
    `reg_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (`file_id`),
    INDEX `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='통합 파일 관리';
