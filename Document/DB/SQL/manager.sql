-- -----------------------------------------------------
-- Manager (Admin)
-- -----------------------------------------------------
CREATE TABLE manager (
    manager_seq      INT AUTO_INCREMENT COMMENT '관리자SEQ',
    manager_code     VARCHAR(30) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    site_cd          VARCHAR(20) NULL COMMENT '사이트코드',
    manager_id       VARCHAR(20) NOT NULL COMMENT '아이디',
    password         VARCHAR(200) NOT NULL COMMENT '비밀번호',
    login_fail_count INT DEFAULT 0 NULL COMMENT '로그인실패횟수',
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    manager_name     VARCHAR(50) NOT NULL COMMENT '이름',
    manager_email    VARCHAR(50) NOT NULL COMMENT '이메일',
    use_yn           CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    apply_yn         CHAR(1) DEFAULT 'N' NOT NULL COMMENT '승인여부',
    description      VARCHAR(200) NULL COMMENT '설명',
    memo             VARCHAR(2000) NULL COMMENT '메모',
    login_date       DATETIME NULL COMMENT '최근로그인일시',
    mb_type          VARCHAR(20) DEFAULT '' NOT NULL COMMENT '관리자유형(MASTER, SCM, ADMIN)',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(20) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(20) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(20) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
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
    menu_url         VARCHAR(100) NULL COMMENT '메뉴URL',
    display_yn       CHAR(1) NOT NULL COMMENT '노출여부',
    display_order    INT NULL COMMENT '표시순서',
    menu_parameter   VARCHAR(100) DEFAULT '' NOT NULL COMMENT '파라미터',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(200) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(200) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(200) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (menu_seq)
) COMMENT '관리자 메뉴';

-- -----------------------------------------------------
-- Manager Auth Group
-- -----------------------------------------------------
CREATE TABLE manager_auth_group (
    auth_group_seq   INT AUTO_INCREMENT COMMENT '권한그룹SEQ',
    auth_group_name  VARCHAR(100) NOT NULL COMMENT '권한그룹명',
    use_yn           CHAR(1) NOT NULL COMMENT '사용여부',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(200) NULL COMMENT '등록자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(200) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(200) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    PRIMARY KEY (auth_group_seq)
) COMMENT '관리자 권한 그룹';

-- -----------------------------------------------------
-- Manager Menu Group Mapping
-- -----------------------------------------------------
CREATE TABLE manager_menu_group (
    auth_group_seq   INT NOT NULL COMMENT '권한그룹SEQ',
    menu_seq         INT NOT NULL COMMENT '메뉴SEQ',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '등록일시',
    created_by       VARCHAR(200) NULL COMMENT '등록자',
    PRIMARY KEY (auth_group_seq, menu_seq),
    CONSTRAINT fk_mmg_auth FOREIGN KEY (auth_group_seq) REFERENCES manager_auth_group (auth_group_seq) ON DELETE CASCADE,
    CONSTRAINT fk_mmg_menu FOREIGN KEY (menu_seq) REFERENCES manager_menu (menu_seq) ON DELETE CASCADE
) COMMENT '관리자 메뉴 권한 매핑';

-- -----------------------------------------------------
-- Manager SCM Information
-- -----------------------------------------------------

CREATE TABLE manager_scm (
    manager_seq           INT NOT NULL COMMENT '관리자SEQ(FK)',
    manager_code          VARCHAR(20) NOT NULL COMMENT '관리자코드(MGR+6자리)',
    supplier_name         VARCHAR(50) NOT NULL COMMENT '공급사명',
    scm_ceo               VARCHAR(50) NOT NULL COMMENT '대표자명',
    scm_corp              VARCHAR(100) NOT NULL COMMENT '법인명',
    scm_type              VARCHAR(10) DEFAULT 'CORP' NOT NULL COMMENT '사업자구분(개인/법인)/ CORP, INDIV',
    scm_bsn               VARCHAR(15) NOT NULL COMMENT '사업자등록번호',
    scm_psn               VARCHAR(30) NULL COMMENT '통신판매업신고번호',
    scm_uptae             VARCHAR(50) NULL COMMENT '업태',
    scm_upjong            VARCHAR(50) NULL COMMENT '업종',
    scm_zipcode           VARCHAR(7) NULL COMMENT '본사우편번호',
    scm_addr1             VARCHAR(100) NULL COMMENT '본사주소',
    scm_addr2             VARCHAR(100) NULL COMMENT '본사상세주소',
    scm_phone             VARCHAR(20) NULL COMMENT '대표전화',
    scm_fax               VARCHAR(20) NULL COMMENT '팩스번호',
    scm_dam_name          VARCHAR(20) NULL COMMENT '담당자명',
    scm_dam_position      VARCHAR(20) NULL COMMENT '담당자직급',
    scm_dam_phone         VARCHAR(20) NULL COMMENT '담당자연락처',
    scm_dam_email         VARCHAR(50) NULL COMMENT '담당자이메일',
    scm_bank_name         VARCHAR(50) NOT NULL COMMENT '은행명',
    scm_bank_account_num  VARCHAR(200) NOT NULL COMMENT '계좌번호(암호화)',
    scm_bank_account_name VARCHAR(50) NOT NULL COMMENT '예금주',
    shipping_zipcode      VARCHAR(10) COMMENT '출고지우편번호',
    shipping_addr1        VARCHAR(200) COMMENT '출고지주소',
    shipping_addr2        VARCHAR(200) COMMENT '출고지상세주소',
    return_zipcode        VARCHAR(10) COMMENT '반품지우편번호',
    return_addr1          VARCHAR(200) COMMENT '반품지주소',
    return_addr2          VARCHAR(200) COMMENT '반품지상세주소',
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    created_by       VARCHAR(20) NULL COMMENT '생성자',
    updated_at       DATETIME NULL COMMENT '수정일시',
    updated_by       VARCHAR(20) NULL COMMENT '수정자',
    deleted_at       DATETIME NULL COMMENT '삭제일시',
    deleted_by       VARCHAR(20) NULL COMMENT '삭제자',
    delete_yn        CHAR(1) DEFAULT 'N' NOT NULL COMMENT '삭제여부',
    CONSTRAINT PK_MANAGER_SCM PRIMARY KEY (manager_seq),
    CONSTRAINT FK_MANAGER_SCM_BASE FOREIGN KEY (manager_seq) REFERENCES manager (manager_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SCM 관리자 상세정보';
