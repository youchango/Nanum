-- Database Initialization Script
-- Order matters due to foreign key constraints (even if logical)

-- 1. System Common & Basics
source basic.sql;
source code.sql;
source file_store.sql;

-- 2. Member & Biz
source member.sql;
source member_biz.sql; -- [NEW] Biz Member Extension
-- source member_availability.sql; -- [DEPRECATED] Worker Availability
-- source attendance.sql; -- [DEPRECATED] Worker Attendance

-- 3. Product Domain (E-Commerce)
source product.sql; -- [NEW] Category, Product, Option, Image
source product_biz_mapping.sql; -- [NEW] Corporate Private Products
source inventory_history.sql; -- [NEW] Stock In/Out History

-- 4. Order & Delivery (E-Commerce)
source order.sql; -- [NEW] Cart, OrderMaster, OrderDetail
source delivery.sql; -- [NEW] Delivery, AddressBook

-- 5. Payment & Benefit
source payment_master.sql; -- [KEEP] Payment History
source point.sql; -- [KEEP] Point System
source coupon.sql; -- [NEW] Coupon System

-- 6. Content & CS
source content.sql;
source inquiry.sql;
source banner.sql;
source popup.sql;

-- 7. Initial Data
source init_data.sql;


-- [LEGACY REMOVED]
-- inspection.sql, member_job.sql, and others have been removed.

