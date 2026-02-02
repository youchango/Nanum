-- Initial Data Setup
-- Password for all initial accounts is '1234'
-- BCrypt Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVwdFYiNu.kZoDDK.z/g5e1a

-- 1. Master Admin
INSERT INTO member (
    member_name, member_login, password, business_number, phone, mobile_phone, 
    zipcode, address, address_detail, email, role, member_type
) VALUES (
    'Master Administrator', 'admin', '$2a$10$gucmZaGXYDF6Nk8DdJRzbe9zAA14dmKlOEHPmZuGsSLCpcYzMYjYS', 
    '000-00-00000', '02-1234-5678', '010-1234-5678', 
    '04524', 'Seoul', 'Gangnam-gu', 'admin@ctso.com', 'ROLE_MASTER', 'ADMIN'
);

-- 2. Biz Partner (Test Account)
INSERT INTO member (
    member_name, member_login, password, business_number, phone, mobile_phone, 
    zipcode, address, address_detail, email, role, member_type
) VALUES (
    'Test Biz Partner', 'test_biz', '$2a$10$tzsZC81JJx0Jhz7MZKHiSeLIQSirLsZU/JpqAymZEgPXrEyLyiXGK', 
    '111-11-11111', '02-111-1111', '010-1111-1111', 
    '11111', 'Biz Office', 'Room 101', 'biz@ctso.com', 'ROLE_BIZ', 'Biz'
);

-- 3. System Codes

-- 서비스 타입 (상위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES ('SERVICE_TYPE', 1, NULL, '서비스 타입', 'Y');

-- 서비스 타입 (하위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES 
  ('SERVICE_TYPE', 2, 1, '정기점검', 'Y'),
  ('SERVICE_TYPE', 2, 1, '소독', 'Y'),
  ('SERVICE_TYPE', 2, 1, '청소', 'Y');

-- 건물 타입 (상위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES ('BUILDING_TYPE', 1, NULL, '건물 타입', 'Y');

-- 건물 타입 (하위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES 
  ('BUILDING_TYPE', 2, 5, '아파트', 'Y'),
  ('BUILDING_TYPE', 2, 5, '빌라', 'Y'),
  ('BUILDING_TYPE', 2, 5, '오피스텔', 'Y'),
  ('BUILDING_TYPE', 2, 5, '단독주택', 'Y'),
  ('BUILDING_TYPE', 2, 5, '상가', 'Y');

-- 문의 유형 (상위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES ('INQUIRY_TYPE', 1, NULL, '문의 유형', 'Y');

-- 문의 유형 (하위 코드)
-- Note: 'upper' value refers to the Code ID of 'INQUIRY_TYPE' (depth=1). 
-- Assuming auto_increment continues, it might differ. Using subquery is safer but for init_data usually fixed IDs or variables are used.
-- Here we assume sequential execution on clean DB. 
-- However, since we cannot use variables easily in simple SQL script without procedural wrapper, 
-- we will use a subquery to find the upper code ID dynamically.
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES 
  ('INQUIRY_TYPE', 2, (SELECT c.code_id FROM (SELECT code_id FROM code WHERE code_type='INQUIRY_TYPE' AND depth=1 LIMIT 1) as c), '시스템 이용 문의', 'Y'),
  ('INQUIRY_TYPE', 2, (SELECT c.code_id FROM (SELECT code_id FROM code WHERE code_type='INQUIRY_TYPE' AND depth=1 LIMIT 1) as c), '계약/결제 문의', 'Y'),
  ('INQUIRY_TYPE', 2, (SELECT c.code_id FROM (SELECT code_id FROM code WHERE code_type='INQUIRY_TYPE' AND depth=1 LIMIT 1) as c), '기타 문의', 'Y');

-- 4. Content Types & Initial Contents

-- 컨텐츠 구분 (상위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES ('CONTENT_TYPE', 1, NULL, '컨텐츠 구분', 'Y');

-- 컨텐츠 구분 (하위 코드)
INSERT INTO code (code_type, depth, upper, code_name, use_yn) 
VALUES 
  ('CONTENT_TYPE', 2, (SELECT c.code_id FROM (SELECT code_id FROM code WHERE code_type='CONTENT_TYPE' AND depth=1 LIMIT 1) as c), '공지사항', 'Y'),
  ('CONTENT_TYPE', 2, (SELECT c.code_id FROM (SELECT code_id FROM code WHERE code_type='CONTENT_TYPE' AND depth=1 LIMIT 1) as c), 'FAQ', 'Y'),
  ('CONTENT_TYPE', 2, (SELECT c.code_id FROM (SELECT code_id FROM code WHERE code_type='CONTENT_TYPE' AND depth=1 LIMIT 1) as c), '서비스소개', 'Y');

-- Insert Contents using Code IDs
INSERT INTO content (content_type, subject, content_body, url_info, created_by)
VALUES
  ((SELECT code_id FROM code WHERE code_name='공지사항' AND depth=2 LIMIT 1), 'CTSO 서비스 오픈 안내', '안녕하세요. CTSO 통합 관리 시스템이 오픈되었습니다.<br>많은 이용 부탁드립니다.', NULL, 1),
  ((SELECT code_id FROM code WHERE code_name='서비스소개' AND depth=2 LIMIT 1), '프리미엄 방역 솔루션', '병원, 학교 등 다중 이용 시설을 위한<br><strong>프리미엄 방역 솔루션</strong>을 소개합니다.', 'http://ctso.com/product', 1),
  ((SELECT code_id FROM code WHERE code_name='공지사항' AND depth=2 LIMIT 1), '정기 점검 서비스 가이드', '월 1회 전문 기사가 방문하여<br>시설물 상태를 점검해드립니다.', NULL, 1);

