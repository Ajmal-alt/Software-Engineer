-- ============================================================
-- ONLINE SHOPPING SYSTEM (OSS)
-- Database Schema + Seed Data
-- Compatible with MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS oss_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE oss_db;

-- ─────────────────────────────────────────────────────────────
-- TABLE: users
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    role       ENUM('ADMIN','MANAGER','CUSTOMER') NOT NULL,
    last_login DATETIME,
    status     BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: customers
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS customers (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT UNIQUE NOT NULL,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    phone         VARCHAR(15),
    date_of_birth DATE,
    gender        ENUM('MALE','FEMALE','OTHER') DEFAULT 'MALE',
    address       TEXT,
    city          VARCHAR(50),
    state         VARCHAR(50),
    pincode       VARCHAR(10),
    loyalty_points INT DEFAULT 0,
    status        ENUM('ACTIVE','INACTIVE','BLOCKED') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: categories
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description   VARCHAR(255),
    status        ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: products
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    product_code  VARCHAR(20) UNIQUE NOT NULL,
    product_name  VARCHAR(200) NOT NULL,
    category_id   INT NOT NULL,
    description   TEXT,
    unit_price    DECIMAL(12,2) NOT NULL,
    cost_price    DECIMAL(12,2),
    stock_qty     INT DEFAULT 0,
    unit          VARCHAR(20) DEFAULT 'Piece',
    brand         VARCHAR(100),
    tax_percent   DECIMAL(5,2) DEFAULT 18.00,
    discount_pct  DECIMAL(5,2) DEFAULT 0.00,
    status        ENUM('ACTIVE','INACTIVE','OUT_OF_STOCK') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: cart
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cart (
    cart_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    product_id  INT NOT NULL,
    quantity    INT DEFAULT 1,
    added_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (product_id)  REFERENCES products(product_id),
    UNIQUE KEY unique_cart_item (customer_id, product_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: orders
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS orders (
    order_id        INT AUTO_INCREMENT PRIMARY KEY,
    order_code      VARCHAR(20) UNIQUE NOT NULL,
    customer_id     INT NOT NULL,
    order_date      DATETIME DEFAULT CURRENT_TIMESTAMP,
    subtotal        DECIMAL(12,2) DEFAULT 0.00,
    tax_amount      DECIMAL(12,2) DEFAULT 0.00,
    discount_amount DECIMAL(12,2) DEFAULT 0.00,
    shipping_charge DECIMAL(8,2)  DEFAULT 0.00,
    total_amount    DECIMAL(12,2) DEFAULT 0.00,
    shipping_address TEXT,
    status          ENUM('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED','CANCELLED','RETURNED') DEFAULT 'PENDING',
    notes           TEXT,
    created_by      INT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (created_by)  REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: order_items
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS order_items (
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    order_id    INT NOT NULL,
    product_id  INT NOT NULL,
    product_name VARCHAR(200),
    quantity    INT NOT NULL,
    unit_price  DECIMAL(12,2) NOT NULL,
    discount_pct DECIMAL(5,2) DEFAULT 0.00,
    tax_percent  DECIMAL(5,2) DEFAULT 0.00,
    line_total   DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: payments
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    payment_id   INT AUTO_INCREMENT PRIMARY KEY,
    payment_ref  VARCHAR(30) UNIQUE NOT NULL,
    order_id     INT NOT NULL,
    customer_id  INT NOT NULL,
    amount       DECIMAL(12,2) NOT NULL,
    payment_mode ENUM('CREDIT_CARD','DEBIT_CARD','UPI','NET_BANKING','WALLET','CASH_ON_DELIVERY') DEFAULT 'UPI',
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status       ENUM('SUCCESS','FAILED','PENDING','REFUNDED') DEFAULT 'SUCCESS',
    remarks      VARCHAR(255),
    processed_by INT,
    FOREIGN KEY (order_id)     REFERENCES orders(order_id),
    FOREIGN KEY (customer_id)  REFERENCES customers(customer_id),
    FOREIGN KEY (processed_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: invoices
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id   INT AUTO_INCREMENT PRIMARY KEY,
    invoice_no   VARCHAR(20) UNIQUE NOT NULL,
    order_id     INT NOT NULL,
    payment_id   INT,
    customer_id  INT NOT NULL,
    invoice_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    subtotal     DECIMAL(12,2),
    tax_amount   DECIMAL(12,2),
    discount     DECIMAL(12,2),
    shipping     DECIMAL(12,2),
    grand_total  DECIMAL(12,2),
    status       ENUM('ISSUED','CANCELLED') DEFAULT 'ISSUED',
    FOREIGN KEY (order_id)    REFERENCES orders(order_id),
    FOREIGN KEY (payment_id)  REFERENCES payments(payment_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: system_logs
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS system_logs (
    log_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id  INT,
    action   VARCHAR(200),
    details  TEXT,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SEED DATA
-- ============================================================

-- Users
INSERT INTO users (username, password, email, role) VALUES
('admin',     'Admin@123', 'admin@oss.com',     'ADMIN'),
('manager1',  'Mgr@123',   'manager1@oss.com',  'MANAGER'),
('customer1', 'Cust@123',  'customer1@oss.com', 'CUSTOMER'),
('customer2', 'Cust@123',  'customer2@oss.com', 'CUSTOMER'),
('customer3', 'Cust@123',  'customer3@oss.com', 'CUSTOMER');

-- Customers
INSERT INTO customers (user_id, customer_code, first_name, last_name, phone,
    date_of_birth, gender, address, city, state, pincode, loyalty_points, status) VALUES
(3, 'CUST-001', 'Arjun',  'Sharma', '9876500001', '1995-04-12', 'MALE',
 '12 MG Road',      'Bangalore', 'Karnataka',   '560001', 250,  'ACTIVE'),
(4, 'CUST-002', 'Priya',  'Menon',  '9876500002', '1992-09-22', 'FEMALE',
 '45 Anna Salai',   'Chennai',   'Tamil Nadu',  '600002', 480,  'ACTIVE'),
(5, 'CUST-003', 'Rahul',  'Gupta',  '9876500003', '1998-03-08', 'MALE',
 '7 Connaught Place','Delhi',    'Delhi',       '110001', 120,  'ACTIVE');

-- Categories
INSERT INTO categories (category_name, description, status) VALUES
('Electronics',     'Mobiles, Laptops, Gadgets and Accessories',  'ACTIVE'),
('Clothing',        'Men, Women and Kids Fashion',                 'ACTIVE'),
('Home & Kitchen',  'Furniture, Appliances and Kitchen items',     'ACTIVE'),
('Books',           'Fiction, Non-Fiction, Academic and more',     'ACTIVE'),
('Sports & Fitness','Equipment, Apparel and Nutrition',            'ACTIVE'),
('Beauty & Care',   'Skincare, Haircare and Personal Hygiene',     'ACTIVE');

-- Products
INSERT INTO products (product_code, product_name, category_id, description,
    unit_price, cost_price, stock_qty, unit, brand, tax_percent, discount_pct, status) VALUES
('PRD-001', 'Samsung Galaxy M34 5G',        1, '6GB RAM, 128GB Storage, 6000mAh Battery',
  18999.00, 15000.00, 50,  'Piece', 'Samsung',   18.00, 5.00,  'ACTIVE'),
('PRD-002', 'Apple AirPods Pro 2nd Gen',     1, 'Active Noise Cancellation, USB-C',
  24900.00, 18000.00, 30,  'Piece', 'Apple',     18.00, 0.00,  'ACTIVE'),
('PRD-003', 'Lenovo IdeaPad Slim 3',         1, 'Intel i5, 8GB RAM, 512GB SSD, 15.6 inch',
  45990.00, 38000.00, 20,  'Piece', 'Lenovo',    18.00, 8.00,  'ACTIVE'),
('PRD-004', 'Levi''s 511 Slim Fit Jeans',   2, 'Stretch denim, comfortable slim fit',
   2499.00,  1400.00, 120, 'Piece', 'Levi''s',   12.00, 10.00, 'ACTIVE'),
('PRD-005', 'Allen Solly Formal Shirt',      2, 'Cotton blend, regular fit, full sleeves',
   1299.00,   700.00, 200, 'Piece', 'Allen Solly',12.00, 15.00, 'ACTIVE'),
('PRD-006', 'Prestige Induction Cooktop',    3, '2000W, Auto-off, LED display',
   2895.00,  1800.00, 60,  'Piece', 'Prestige',  18.00, 5.00,  'ACTIVE'),
('PRD-007', 'Atomic Habits - James Clear',   4, 'Bestselling self-help book',
    499.00,   200.00, 300, 'Piece', 'Penguin',    0.00, 0.00,  'ACTIVE'),
('PRD-008', 'Boldfit Gym Gloves',            5, 'Anti-slip, wrist support, unisex',
    399.00,   180.00, 150, 'Pair',  'Boldfit',   18.00, 10.00, 'ACTIVE'),
('PRD-009', 'Mamaearth Vitamin C Face Wash', 6, '100ml, Brightening, Paraben-free',
    299.00,   120.00, 250, 'Piece', 'Mamaearth',  0.00, 5.00,  'ACTIVE'),
('PRD-010', 'Sony WH-1000XM5 Headphones',   1, 'Industry-leading noise cancellation',
  28990.00, 22000.00, 15,  'Piece', 'Sony',      18.00, 0.00,  'ACTIVE');

-- Cart items for customer1
INSERT INTO cart (customer_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 7, 2);

-- Orders
INSERT INTO orders (order_code, customer_id, order_date, subtotal, tax_amount,
    discount_amount, shipping_charge, total_amount, shipping_address, status, created_by) VALUES
('ORD-2026-001', 1, '2026-03-01 10:30:00',  25398.00, 2498.00, 1270.00, 0.00,   26626.00,
 '12 MG Road, Bangalore, Karnataka 560001',  'DELIVERED', 2),
('ORD-2026-002', 2, '2026-03-05 14:15:00',  26199.00, 2520.00, 0.00,    0.00,   28719.00,
 '45 Anna Salai, Chennai, Tamil Nadu 600002','SHIPPED',   2),
('ORD-2026-003', 1, '2026-03-15 09:00:00',   3194.00,   83.88, 374.85,  49.00,   2952.03,
 '12 MG Road, Bangalore, Karnataka 560001',  'CONFIRMED', 2),
('ORD-2026-004', 3, '2026-03-20 16:45:00',   1798.00,  215.76, 179.80,  0.00,    1833.96,
 '7 Connaught Place, Delhi 110001',          'PENDING',   2);

-- Order Items
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, discount_pct, tax_percent, line_total) VALUES
(1, 1, 'Samsung Galaxy M34 5G',       1, 18999.00, 5.00, 18.00, 21349.87),
(1, 3, 'Lenovo IdeaPad Slim 3',       0, 45990.00, 8.00, 18.00,  0.00),
(1, 7, 'Atomic Habits - James Clear', 2,   499.00, 0.00,  0.00,  998.00),
(2, 2, 'Apple AirPods Pro 2nd Gen',   1, 24900.00, 0.00, 18.00, 29382.00),
(3, 4, 'Levi''s 511 Slim Fit Jeans',  1,  2499.00,10.00, 12.00,  2518.88),
(3, 9, 'Mamaearth Vitamin C Face Wash',2,   299.00, 5.00,  0.00,   568.10),
(4, 6, 'Prestige Induction Cooktop',  1,  2895.00, 5.00, 18.00,  3237.21),
(4, 8, 'Boldfit Gym Gloves',          1,   399.00,10.00, 18.00,   423.34);

-- Payments
INSERT INTO payments (payment_ref, order_id, customer_id, amount, payment_mode,
    payment_date, status, remarks, processed_by) VALUES
('PAY-2026-0001', 1, 1, 26626.00, 'UPI',           '2026-03-01 10:35:00', 'SUCCESS', 'Paid via GPay',    2),
('PAY-2026-0002', 2, 2, 28719.00, 'CREDIT_CARD',   '2026-03-05 14:20:00', 'SUCCESS', 'Visa card payment',2),
('PAY-2026-0003', 3, 1,  2952.03, 'NET_BANKING',   '2026-03-15 09:05:00', 'SUCCESS', 'HDFC Net Banking', 2);

-- Invoices
INSERT INTO invoices (invoice_no, order_id, payment_id, customer_id,
    invoice_date, subtotal, tax_amount, discount, shipping, grand_total, status) VALUES
('INV-2026-001', 1, 1, 1, '2026-03-01 10:36:00', 25398.00, 2498.00, 1270.00, 0.00,   26626.00, 'ISSUED'),
('INV-2026-002', 2, 2, 2, '2026-03-05 14:21:00', 26199.00, 2520.00,    0.00, 0.00,   28719.00, 'ISSUED'),
('INV-2026-003', 3, 3, 1, '2026-03-15 09:06:00',  3194.00,   83.88,  374.85,49.00,    2952.03, 'ISSUED');

COMMIT;
