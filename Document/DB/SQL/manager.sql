-- -----------------------------------------------------
-- Manager (Admin)
-- -----------------------------------------------------
CREATE TABLE manager (
    manager_seq      INT AUTO_INCREMENT COMMENT '관리자SEQ',
    manager_code     VARCHAR(30) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    site_cd          VARCHAR(20) DEFAULT 'SITECD000001' NULL COMMENT '사이트코드',
    manager_id       VARCHAR(20) NOT NULL COMMENT '아이디',
    password         VARCHAR(200) NOT NULL COMMENT '비밀번호',
    login_fail_count INT DEFAULT 0 NULL COMMENT '로그인실패횟수',
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    manager_name     VARCHAR(50) NOT NULL COMMENT '이름',
    manager_email    VARCHAR(50) NOT NULL COMMENT '이메일',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    apply_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '승인여부',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    description      VARCHAR(200) NULL COMMENT '설명',
    regist_by        VARCHAR(20) NOT NULL COMMENT '등록자',
    regist_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    update_by        VARCHAR(20) NOT NULL COMMENT '수정자',
    update_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    login_date       DATETIME NULL COMMENT '최근로그인일시',
    mb_type          VARCHAR(20) DEFAULT '' NOT NULL COMMENT '관리자유형(MASTER, SCM, ADMIN)',
    PRIMARY KEY (manager_seq),
    UNIQUE KEY uq_manager_id (manager_id),
    UNIQUE KEY uq_manager_code (manager_code)
) COMMENT '관리자 정보';

-- -----------------------------------------------------
-- Manager Menu
-- -----------------------------------------------------
CREATE TABLE manager_menu (
    menu_seq         INT AUTO_INCREMENT COMMENT '메뉴SEQ',
    parent_menu_seq  INT NULL COMMENT '상위메뉴SEQ',
    menu_name        VARCHAR(100) NOT NULL COMMENT '메뉴명',
    program_url      VARCHAR(100) NULL COMMENT '프로그램URL',
    display_yn       CHAR(1) NOT NULL COMMENT '노출여부',
    display_order    INT NULL COMMENT '표시순서',
    regist_by        VARCHAR(200) NOT NULL COMMENT '등록자',
    regist_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    update_by        VARCHAR(200) NOT NULL COMMENT '수정자',
    update_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    program_parameter VARCHAR(100) DEFAULT '' NOT NULL COMMENT '파라미터',
    PRIMARY KEY (menu_seq)
) COMMENT '관리자 메뉴';

-- -----------------------------------------------------
-- Manager Auth Group
-- -----------------------------------------------------
CREATE TABLE manager_auth_group (
    auth_group_seq   INT AUTO_INCREMENT COMMENT '권한그룹SEQ',
    auth_group_name  VARCHAR(100) NOT NULL COMMENT '권한그룹명',
    use_yn           CHAR(1) NOT NULL COMMENT '사용여부',
    regist_by        VARCHAR(200) NOT NULL COMMENT '등록자',
    regist_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    update_by        VARCHAR(200) NOT NULL COMMENT '수정자',
    update_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '수정일시',
    PRIMARY KEY (auth_group_seq)
) COMMENT '관리자 권한 그룹';

-- -----------------------------------------------------
-- Manager Menu Group Mapping
-- -----------------------------------------------------
CREATE TABLE manager_menu_group (
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    menu_seq         INT NOT NULL COMMENT '메뉴SEQ',
    regist_by        VARCHAR(200) NOT NULL COMMENT '등록자',
    regist_date      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    PRIMARY KEY (auth_group_seq, menu_seq),
    CONSTRAINT fk_mmg_auth FOREIGN KEY (auth_group_seq) REFERENCES manager_auth_group (auth_group_seq) ON DELETE CASCADE,
    CONSTRAINT fk_mmg_menu FOREIGN KEY (menu_seq) REFERENCES manager_menu (menu_seq) ON DELETE CASCADE
) COMMENT '관리자 메뉴 권한 매핑';
