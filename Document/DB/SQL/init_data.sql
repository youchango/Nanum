-- ==========================================
-- Nanum Shopping Mall Platform Initial Data
-- ==========================================

-- 1. Members
-- Admin
INSERT INTO member (member_code, member_login, member_name, password, mobile_phone, email, role, member_type)
VALUES ('M_ADMIN_001', 'admin', 'Master Admin', '$2a$10$gucmZaGXYDF6Nk8DdJRzbe9zAA14dmKlOEHPmZuGsSLCpcYzMYjYS', '010-1234-5678', 'admin@nanum.com', 'ROLE_MASTER', 'ADMIN');

-- Biz User
INSERT INTO member (member_code, member_login, member_name, password, mobile_phone, email, zipcode, address, address_detail, role, member_type)
VALUES ('M_BIZ_001', 'bizuser', 'Biz Partner', '$2a$10$tzsZC81JJx0Jhz7MZKHiSeLIQSirLsZU/JpqAymZEgPXrEyLyiXGK', '010-1111-2222', 'biz@nanum.com', '12345', 'Seoul', 'Gangnam 123', 'ROLE_BIZ', 'BIZ');

-- General User
INSERT INTO member (member_code, member_login, member_name, password, mobile_phone, email, zipcode, address, address_detail, role, member_type)
VALUES ('M_USER_001', 'user01', 'Normal User', '$2a$10$tzsZC81JJx0Jhz7MZKHiSeLIQSirLsZU/JpqAymZEgPXrEyLyiXGK', '010-3333-4444', 'user@nanum.com', '54321', 'Busan', 'Haeundae 456', 'ROLE_USER', 'USER');

-- 2. Member Biz Info
INSERT INTO member_biz (member_code, company_name, ceo_name, business_number, approval_status)
VALUES ('M_BIZ_001', 'Nanum Corp', 'Kim Biz', '123-45-67890', 'APPROVED');

-- 3. Address Book
INSERT INTO address_book (member_code, address_name, receiver_name, receiver_phone, zipcode, address, address_detail, is_default)
VALUES ('M_USER_001', 'Home', 'Normal User', '010-3333-4444', '54321', 'Busan', 'Haeundae 456', 'Y');

-- 4. Product Categories
INSERT INTO product_category (category_name, depth, display_order) VALUES ('Electronics', 1, 1);
INSERT INTO product_category (category_name, depth, display_order) VALUES ('Fashion', 1, 2);
INSERT INTO product_category (parent_id, category_name, depth, display_order) VALUES (1, 'Computers', 2, 1);
INSERT INTO product_category (parent_id, category_name, depth, display_order) VALUES (1, 'Smartphones', 2, 2);

-- 5. Products
-- Public Product
INSERT INTO product (category_id, product_name, price, sale_price, status, description, thumbnail_url)
VALUES (3, 'Gaming Laptop', 1500000, 1350000, 'SALE', 'High performance gaming laptop', '/uploads/laptop.jpg');

-- Biz Only Product (Only mapped users can see discount? Or mapping logic)
INSERT INTO product (category_id, product_name, price, sale_price, status, description, thumbnail_url)
VALUES (4, 'Biz Smartphone', 1000000, 900000, 'SALE', 'Corporate optimized phone', '/uploads/phone.jpg');

-- 6. Product Options
INSERT INTO product_option (product_id, option_name, extra_price, stock_quantity)
VALUES (1, 'RAM 16GB', 0, 100);
INSERT INTO product_option (product_id, option_name, extra_price, stock_quantity)
VALUES (1, 'RAM 32GB', 200000, 50);

-- 7. Product Biz Mapping
INSERT INTO product_biz_mapping (product_id, member_code, discount_rate)
VALUES (2, 'M_BIZ_001', 10); -- 10% discount for this biz member

-- 8. Content (Notice)
INSERT INTO content (content_type, subject, body)
VALUES ('NOTICE', 'Nanum Mall Open!', 'Welcome to Nanum Shopping Mall.');

-- 9. Inquiry
INSERT INTO inquiry (member_code, inquiry_type, title, content, status)
VALUES ('M_USER_001', 'General', 'Delivery Question', 'When does shipping start?', 'WAITING');
