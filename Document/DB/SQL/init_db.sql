-- ==========================================
-- Nanum Shopping Mall Platform Initialization
-- ==========================================

-- 1. Database & User Setup (If needed manually)
-- CREATE DATABASE IF NOT EXISTS db_nanum;

-- 2. Cleanup Legacy Tables
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS member_availability;
DROP TABLE IF EXISTS work_schedule;
DROP TABLE IF EXISTS worker_location;
DROP TABLE IF EXISTS delivery_route;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS center;

-- 3. Core Tables

-- Member (Users)
CREATE TABLE IF NOT EXISTS member (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_name VARCHAR(100) NOT NULL,
    member_login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    mobile_phone VARCHAR(20),
    zipcode VARCHAR(10),
    address VARCHAR(255),
    address_detail VARCHAR(255),
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'ROLE_USER', -- ROLE_MASTER, ROLE_BIZ, ROLE_USER
    member_type VARCHAR(20) DEFAULT 'USER', -- ADMIN, BIZ, USER
    withdraw_yn CHAR(1) DEFAULT 'N',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Member Biz (Corporate Details)
CREATE TABLE IF NOT EXISTS member_biz (
    biz_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    company_name VARCHAR(100),
    business_number VARCHAR(20),
    representative_name VARCHAR(50),
    company_address VARCHAR(255),
    approval_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Delivery Address (Multiple Addresses per User)
CREATE TABLE IF NOT EXISTS user_address (
    address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    address_name VARCHAR(50), -- e.g. "Office", "Home", "Site A"
    recipient_name VARCHAR(50),
    mobile_phone VARCHAR(20),
    zipcode VARCHAR(10),
    address VARCHAR(255),
    address_detail VARCHAR(255),
    is_default CHAR(1) DEFAULT 'N',
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Product Category
CREATE TABLE IF NOT EXISTS product_category (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    depth INT DEFAULT 1,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES product_category(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Product
CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT,
    product_name VARCHAR(200) NOT NULL,
    price DECIMAL(10, 0) NOT NULL,
    description TEXT,
    main_image_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ON_SALE', -- ON_SALE, SOLD_OUT, STOPPED
    is_biz_only CHAR(1) DEFAULT 'N', -- If 'Y', visible only to Biz members
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES product_category(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Product Option
CREATE TABLE IF NOT EXISTS product_option (
    option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    option_name VARCHAR(100) NOT NULL,
    add_price DECIMAL(10, 0) DEFAULT 0,
    stock_quantity INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cart
CREATE TABLE IF NOT EXISTS cart (
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    option_id BIGINT,
    quantity INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Order Master
CREATE TABLE IF NOT EXISTS order_master (
    order_id VARCHAR(50) PRIMARY KEY, -- YYYYMMDD-RANDOM
    member_id BIGINT NOT NULL,
    order_name VARCHAR(200), -- "Product A and 3 others"
    total_amount DECIMAL(12, 0) NOT NULL,
    status VARCHAR(20) DEFAULT 'PAYMENT_WAIT', -- PAYMENT_WAIT, PAID, PREPARING, SHIPPING, DELIVERED, CANCELLED
    recipient_name VARCHAR(50),
    recipient_phone VARCHAR(20),
    shipping_address VARCHAR(255),
    shipping_address_detail VARCHAR(255),
    shipping_zipcode VARCHAR(10),
    delivery_msg VARCHAR(200),
    tracking_number VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Order Detail
CREATE TABLE IF NOT EXISTS order_detail (
    detail_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    option_id BIGINT,
    quantity INT NOT NULL,
    price_per_unit DECIMAL(10, 0) NOT NULL,
    total_price DECIMAL(10, 0) NOT NULL,
    status VARCHAR(20) DEFAULT 'ORDERED',
    FOREIGN KEY (order_id) REFERENCES order_master(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Banner (Marketing)
CREATE TABLE IF NOT EXISTS banner (
    banner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    image_url VARCHAR(255),
    link_url VARCHAR(255),
    position VARCHAR(20) DEFAULT 'MAIN_TOP',
    sort_order INT DEFAULT 0,
    is_active CHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inquiry (1:1 Support)
CREATE TABLE IF NOT EXISTS inquiry (
    inquiry_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    answer TEXT,
    status VARCHAR(20) DEFAULT 'WAITING', -- WAITING, ANSWERED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    answered_at DATETIME,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
