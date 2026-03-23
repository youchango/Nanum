-- 🏗️ 관리자 메뉴 초기 데이터 (AdminLayout.tsx 기반)

-- 기존 데이터 정리 (필요시)
-- DELETE FROM manager_menu;
-- ALTER TABLE manager_menu AUTO_INCREMENT = 1;

-- 1. 대시보드
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '대시보드', '/admin/dashboard', 'Y', 1, '', 'SYSTEM');

-- 2. 상점 관리 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '상점 관리', NULL, 'Y', 2, '', 'SYSTEM');
SET @shop_parent_seq = (SELECT LAST_INSERT_ID());

    -- 상점 관리 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@shop_parent_seq, '상점 목록', '/admin/shops', 'Y', 1, '', 'SYSTEM');

-- 3. 회원 관리 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '회원 관리', NULL, 'Y', 3, '', 'SYSTEM');
SET @member_parent_seq = (SELECT LAST_INSERT_ID());

    -- 회원 관리 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@member_parent_seq, '회원 목록', '/admin/members/list', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@member_parent_seq, '관리자 관리', '/admin/members/managers', 'Y', 2, '', 'SYSTEM');

-- 4. 상품 관리 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '상품 관리', NULL, 'Y', 4, '', 'SYSTEM');
SET @product_parent_seq = (SELECT LAST_INSERT_ID());

    -- 상품 관리 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@product_parent_seq, '상품 목록', '/admin/products', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@product_parent_seq, '상품 가격 관리', '/admin/product-sites', 'Y', 2, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@product_parent_seq, '카테고리 관리', '/admin/products/categories', 'Y', 3, '', 'SYSTEM');

-- 5. 주문/배송 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '주문/배송', NULL, 'Y', 5, '', 'SYSTEM');
SET @order_parent_seq = (SELECT LAST_INSERT_ID());

    -- 주문/배송 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@order_parent_seq, '주문 관리', '/admin/orders', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@order_parent_seq, '배송 관리', '/admin/delivery', 'Y', 2, '', 'SYSTEM');

-- 6. 결제 관리 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '결제 관리', NULL, 'Y', 6, '', 'SYSTEM');
SET @payment_parent_seq = (SELECT LAST_INSERT_ID());

    -- 결제 관리 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@payment_parent_seq, '결제 목록', '/admin/payments', 'Y', 1, '', 'SYSTEM');

-- 7. 입출고 관리 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '입출고 관리', NULL, 'Y', 7, '', 'SYSTEM');
SET @inout_parent_seq = (SELECT LAST_INSERT_ID());

    -- 입출고 관리 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@inout_parent_seq, '입고 관리', '/admin/inout/inbound', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@inout_parent_seq, '출고 관리', '/admin/inout/outbound', 'Y', 2, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@inout_parent_seq, '입출고 현황', '/admin/inout/status', 'Y', 3, '', 'SYSTEM');

-- 8. 마케팅 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '마케팅', NULL, 'Y', 8, '', 'SYSTEM');
SET @marketing_parent_seq = (SELECT LAST_INSERT_ID());

    -- 마케팅 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@marketing_parent_seq, '배너 관리', '/admin/display/banners', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@marketing_parent_seq, '팝업 관리', '/admin/display/popups', 'Y', 2, '', 'SYSTEM');

-- 9. 고객센터 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '고객센터', NULL, 'Y', 9, '', 'SYSTEM');
SET @cs_parent_seq = (SELECT LAST_INSERT_ID());

    -- 고객센터 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@cs_parent_seq, '1:1 문의', '/admin/inquiries', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@cs_parent_seq, '공지사항 / FAQ', '/admin/contents/notices', 'Y', 2, '', 'SYSTEM');

-- 10. 메뉴/권한 설정 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '메뉴/권한 설정', NULL, 'Y', 10, '', 'SYSTEM');
SET @auth_parent_seq = (SELECT LAST_INSERT_ID());

    -- 메뉴/권한 설정 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@auth_parent_seq, '권한 관리', '/admin/manager/auth-groups', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@auth_parent_seq, '메뉴 관리', '/admin/manager/menus', 'Y', 2, '', 'SYSTEM');

-- 11. 시스템 설정 (대메뉴)
INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
VALUES (NULL, '시스템 설정', NULL, 'Y', 11, '', 'SYSTEM');
SET @config_parent_seq = (SELECT LAST_INSERT_ID());

    -- 시스템 설정 하위
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@config_parent_seq, '코드 관리', '/admin/codes', 'Y', 1, '', 'SYSTEM');
    INSERT INTO manager_menu (parent_menu_seq, menu_name, menu_url, display_yn, display_order, menu_parameter, created_by)
    VALUES (@config_parent_seq, '정책/약관 관리', '/admin/system/settings', 'Y', 2, '', 'SYSTEM');


