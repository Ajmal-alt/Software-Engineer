-- ============================================================
-- MARKETING MANAGEMENT SYSTEM (MMS)
-- Database Schema + Seed Data
-- Compatible with MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS mms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mms_db;

-- ─────────────────────────────────────────────────────────────
-- TABLE: users
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    role       ENUM('ADMIN','MANAGER','AGENT') NOT NULL,
    last_login DATETIME,
    status     BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: leads
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS leads (
    lead_id        INT AUTO_INCREMENT PRIMARY KEY,
    lead_code      VARCHAR(20) UNIQUE NOT NULL,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    email          VARCHAR(100),
    phone          VARCHAR(15),
    company        VARCHAR(100),
    designation    VARCHAR(100),
    city           VARCHAR(50),
    state          VARCHAR(50),
    source         ENUM('WEBSITE','REFERRAL','SOCIAL_MEDIA','EMAIL_CAMPAIGN','COLD_CALL','EVENT','OTHER') DEFAULT 'OTHER',
    interest_area  VARCHAR(100),
    budget_range   VARCHAR(50),
    status         ENUM('NEW','CONTACTED','QUALIFIED','PROPOSAL_SENT','NEGOTIATION','CONVERTED','LOST') DEFAULT 'NEW',
    priority       ENUM('LOW','MEDIUM','HIGH') DEFAULT 'MEDIUM',
    notes          TEXT,
    assigned_to    INT,
    created_by     INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id),
    FOREIGN KEY (created_by)  REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: customers
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS customers (
    customer_id    INT AUTO_INCREMENT PRIMARY KEY,
    customer_code  VARCHAR(20) UNIQUE NOT NULL,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    email          VARCHAR(100),
    phone          VARCHAR(15),
    company        VARCHAR(100),
    designation    VARCHAR(100),
    address        TEXT,
    city           VARCHAR(50),
    state          VARCHAR(50),
    pincode        VARCHAR(10),
    customer_type  ENUM('INDIVIDUAL','CORPORATE','SME','ENTERPRISE') DEFAULT 'INDIVIDUAL',
    segment        ENUM('PREMIUM','STANDARD','BASIC') DEFAULT 'STANDARD',
    total_purchases DECIMAL(15,2) DEFAULT 0.00,
    loyalty_points  INT DEFAULT 0,
    status         ENUM('ACTIVE','INACTIVE','BLOCKED') DEFAULT 'ACTIVE',
    lead_id        INT,
    assigned_to    INT,
    created_by     INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lead_id)     REFERENCES leads(lead_id),
    FOREIGN KEY (assigned_to) REFERENCES users(user_id),
    FOREIGN KEY (created_by)  REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: lead_activities  (follow-up log)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS lead_activities (
    activity_id    INT AUTO_INCREMENT PRIMARY KEY,
    lead_id        INT NOT NULL,
    activity_type  ENUM('CALL','EMAIL','MEETING','DEMO','PROPOSAL','FOLLOW_UP','NOTE') DEFAULT 'NOTE',
    description    TEXT,
    outcome        VARCHAR(255),
    activity_date  DATETIME DEFAULT CURRENT_TIMESTAMP,
    next_action    VARCHAR(255),
    next_action_date DATE,
    performed_by   INT,
    FOREIGN KEY (lead_id)      REFERENCES leads(lead_id),
    FOREIGN KEY (performed_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: products
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    product_id     INT AUTO_INCREMENT PRIMARY KEY,
    product_code   VARCHAR(20) UNIQUE NOT NULL,
    product_name   VARCHAR(200) NOT NULL,
    category       VARCHAR(100),
    description    TEXT,
    unit_price     DECIMAL(12,2) NOT NULL,
    cost_price     DECIMAL(12,2),
    stock_quantity INT DEFAULT 0,
    unit           VARCHAR(20) DEFAULT 'Unit',
    brand          VARCHAR(100),
    status         ENUM('ACTIVE','INACTIVE','DISCONTINUED') DEFAULT 'ACTIVE',
    created_by     INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: promotions
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS promotions (
    promo_id       INT AUTO_INCREMENT PRIMARY KEY,
    promo_code     VARCHAR(20) UNIQUE NOT NULL,
    promo_name     VARCHAR(200) NOT NULL,
    promo_type     ENUM('PERCENTAGE_DISCOUNT','FLAT_DISCOUNT','BUY_X_GET_Y','FREE_SHIPPING','BUNDLE') DEFAULT 'PERCENTAGE_DISCOUNT',
    discount_value DECIMAL(10,2),
    min_purchase   DECIMAL(10,2) DEFAULT 0.00,
    max_discount   DECIMAL(10,2),
    start_date     DATE NOT NULL,
    end_date       DATE NOT NULL,
    usage_limit    INT DEFAULT 100,
    usage_count    INT DEFAULT 0,
    applicable_to  ENUM('ALL','PREMIUM','CORPORATE','SME') DEFAULT 'ALL',
    description    TEXT,
    status         ENUM('ACTIVE','INACTIVE','EXPIRED') DEFAULT 'ACTIVE',
    created_by     INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: product_promotions  (many-to-many)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS product_promotions (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    product_id     INT NOT NULL,
    promo_id       INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (promo_id)   REFERENCES promotions(promo_id),
    UNIQUE KEY unique_pp (product_id, promo_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: orders  (customer purchases)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS orders (
    order_id       INT AUTO_INCREMENT PRIMARY KEY,
    order_code     VARCHAR(20) UNIQUE NOT NULL,
    customer_id    INT NOT NULL,
    order_date     DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount   DECIMAL(12,2) DEFAULT 0.00,
    discount_amount DECIMAL(12,2) DEFAULT 0.00,
    final_amount   DECIMAL(12,2) DEFAULT 0.00,
    promo_id       INT,
    status         ENUM('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED','RETURNED') DEFAULT 'PENDING',
    notes          TEXT,
    processed_by   INT,
    FOREIGN KEY (customer_id)  REFERENCES customers(customer_id),
    FOREIGN KEY (promo_id)     REFERENCES promotions(promo_id),
    FOREIGN KEY (processed_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: order_items
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS order_items (
    item_id        INT AUTO_INCREMENT PRIMARY KEY,
    order_id       INT NOT NULL,
    product_id     INT NOT NULL,
    quantity       INT NOT NULL,
    unit_price     DECIMAL(12,2) NOT NULL,
    total_price    DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
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
('admin',   'Admin@123', 'admin@mms.com',   'ADMIN'),
('manager1','Mgr@123',   'manager1@mms.com','MANAGER'),
('agent1',  'Agt@123',   'agent1@mms.com',  'AGENT'),
('agent2',  'Agt@123',   'agent2@mms.com',  'AGENT');

-- Products
INSERT INTO products (product_code, product_name, category, description, unit_price, cost_price, stock_quantity, unit, brand, status, created_by) VALUES
('PRD-001','Marketing Analytics Suite','Software',  'Complete analytics platform for campaigns',    45000.00, 20000.00, 50,  'License','MarketPro', 'ACTIVE',2),
('PRD-002','CRM Enterprise Package',   'Software',  'Customer relationship management software',   75000.00, 35000.00, 30,  'License','CRMSoft',  'ACTIVE',2),
('PRD-003','Email Campaign Tool',      'Software',  'Bulk email marketing automation tool',         8500.00,  3000.00, 200, 'License','MailBlast', 'ACTIVE',2),
('PRD-004','Social Media Manager',     'Software',  'Schedule and manage social media posts',      12000.00,  5000.00, 150, 'License','SocialPro', 'ACTIVE',2),
('PRD-005','Marketing Consulting',     'Service',   '1-day on-site marketing strategy session',   25000.00, 10000.00, 100, 'Session','MMS Team',  'ACTIVE',2),
('PRD-006','SEO Optimization Package', 'Service',   '3-month SEO and content strategy service',   18000.00,  7000.00,  80, 'Package','MMS Team',  'ACTIVE',2);

-- Leads
INSERT INTO leads (lead_code, first_name, last_name, email, phone, company, designation, city, state, source, interest_area, budget_range, status, priority, assigned_to, created_by) VALUES
('LED-001','Vikram',  'Nair',    'vikram@techco.com',  '9876001001','TechCo India',   'CEO',         'Mumbai',    'Maharashtra','WEBSITE',        'CRM Software',       '50k-1L',  'QUALIFIED',     'HIGH',  3,2),
('LED-002','Ananya',  'Reddy',   'ananya@retail.com',  '9876001002','RetailKing',     'Marketing Head','Hyderabad','Telangana',  'EMAIL_CAMPAIGN', 'Email Marketing',    '10k-50k', 'CONTACTED',     'MEDIUM',3,2),
('LED-003','Suresh',  'Pillai',  'suresh@mfg.com',     '9876001003','PrecisionMfg',   'GM Sales',    'Chennai',   'Tamil Nadu', 'REFERRAL',       'Analytics Platform', '1L-5L',   'PROPOSAL_SENT', 'HIGH',  4,2),
('LED-004','Meera',   'Joshi',   'meera@startup.com',  '9876001004','StartupHub',     'Founder',     'Pune',      'Maharashtra','SOCIAL_MEDIA',   'Social Media Tool',  '10k-50k', 'NEW',           'LOW',   4,2),
('LED-005','Arjun',   'Kapoor',  'arjun@finance.com',  '9876001005','FinVenture',     'Director',    'Delhi',     'Delhi',      'COLD_CALL',      'Full Suite',         '5L+',     'NEGOTIATION',   'HIGH',  3,2),
('LED-006','Deepika', 'Singh',   'deepika@edu.com',    '9876001006','EduTech Co',     'VP Marketing','Bangalore', 'Karnataka',  'EVENT',          'Email + SEO',        '50k-1L',  'CONVERTED',     'HIGH',  3,2),
('LED-007','Rajan',   'Mehta',   'rajan@logistics.com','9876001007','FastLogistics',  'MD',          'Ahmedabad', 'Gujarat',    'WEBSITE',        'CRM Software',       '1L-5L',   'LOST',          'MEDIUM',4,2);

-- Customers (converted from leads)
INSERT INTO customers (customer_code, first_name, last_name, email, phone, company, designation,
    address, city, state, pincode, customer_type, segment, total_purchases, loyalty_points, status, lead_id, assigned_to, created_by) VALUES
('CUS-001','Deepika','Singh',  'deepika@edu.com',   '9876001006','EduTech Co',   'VP Marketing',
 '45 Tech Park, Whitefield','Bangalore','Karnataka','560066','CORPORATE','PREMIUM', 103500.00,1035,'ACTIVE',6,3,2),
('CUS-002','Priya',  'Sharma', 'priya@medico.com',  '9877001001','MedicoPlus',   'CEO',
 '12 Hospital Road',        'Delhi',    'Delhi',    '110001','ENTERPRISE','PREMIUM',225000.00,2250,'ACTIVE',NULL,2,2),
('CUS-003','Rahul',  'Verma',  'rahul@sme.com',     '9877001002','SME Solutions', 'Owner',
 '7 MIDC Estate',           'Pune',     'Maharashtra','411018','SME',      'STANDARD', 52000.00, 520,'ACTIVE',NULL,4,2),
('CUS-004','Neha',   'Agarwal','neha@fashion.com',  '9877001003','FashionRetail', 'Director',
 '88 Commercial Street',    'Mumbai',   'Maharashtra','400001','CORPORATE','STANDARD', 33500.00, 335,'ACTIVE',NULL,3,2);

-- Lead Activities
INSERT INTO lead_activities (lead_id, activity_type, description, outcome, next_action, next_action_date, performed_by) VALUES
(1,'CALL',   'Initial discovery call to understand requirements','Interested in CRM package, asked for demo','Schedule product demo','2026-04-05',3),
(1,'DEMO',   'Product demo conducted over video call',          'Positive feedback, requesting proposal',  'Send detailed proposal','2026-04-10',3),
(2,'EMAIL',  'Sent product catalogue and pricing',              'Read the email, no response yet',          'Follow up call',        '2026-04-03',3),
(3,'PROPOSAL','Sent detailed proposal with ROI analysis',       'Under review by management',               'Follow up in 1 week',   '2026-04-07',4),
(5,'MEETING','In-person meeting at client office',              'Price negotiation in progress',            'Final negotiation call', '2026-04-04',3),
(6,'CALL',   'Closing call — confirmed purchase',               'Order placed for Email Tool + SEO package','Send invoice',          '2026-03-28',3);

-- Promotions
INSERT INTO promotions (promo_code, promo_name, promo_type, discount_value, min_purchase, max_discount, start_date, end_date, usage_limit, usage_count, applicable_to, description, status, created_by) VALUES
('PROMO-CORP20','Corporate 20% Off',   'PERCENTAGE_DISCOUNT',20.00, 50000.00, 20000.00,'2026-01-01','2026-12-31',50, 8,'CORPORATE','20% off for corporate clients on orders above Rs.50,000',     'ACTIVE',2),
('PROMO-FLAT5K', 'Flat Rs.5000 Off',   'FLAT_DISCOUNT',      5000.00,20000.00, 5000.00, '2026-01-01','2026-06-30',100,12,'ALL',      'Flat Rs.5000 discount on any order above Rs.20,000',            'ACTIVE',2),
('PROMO-BUNDLE', 'Analytics Bundle',   'BUNDLE',             15.00, 80000.00,15000.00, '2026-02-01','2026-08-31',30,  3,'ALL',      'Buy Analytics + CRM together get 15% off',                      'ACTIVE',2),
('PROMO-Q4SALE', 'Q4 Year End Sale',   'PERCENTAGE_DISCOUNT',25.00, 10000.00,30000.00, '2025-10-01','2025-12-31',200,45,'ALL',      'Year end 25% clearance sale (Expired)',                          'EXPIRED',2),
('PROMO-SME10',  'SME Special 10% Off','PERCENTAGE_DISCOUNT',10.00, 15000.00,10000.00, '2026-03-01','2026-09-30',80,  5,'SME',      '10% discount exclusively for SME segment customers',             'ACTIVE',2);

-- Product-Promotion links
INSERT INTO product_promotions (product_id, promo_id) VALUES
(1,1),(1,3),(2,1),(2,3),(3,2),(3,5),(4,2),(4,5),(5,1),(6,2);

-- Orders
INSERT INTO orders (order_code, customer_id, total_amount, discount_amount, final_amount, promo_id, status, notes, processed_by) VALUES
('ORD-001',1, 53500.00, 10700.00, 42800.00, 1,'DELIVERED','CRM + Email Tool bundle for EduTech',3),
('ORD-002',1, 60700.00, 0.00,     60700.00, NULL,'DELIVERED','SEO Package + Consulting session',  3),
('ORD-003',2,225000.00,45000.00, 180000.00, 1,'DELIVERED','Full enterprise suite purchase',       2),
('ORD-004',3, 52000.00, 0.00,     52000.00, NULL,'DELIVERED','Marketing Analytics + Email Tool',  4),
('ORD-005',4, 33500.00, 5000.00,  28500.00, 2,'CONFIRMED','Social Media Manager + Consulting',    3);

-- Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES
(1,2,1,75000.00,75000.00),(1,3,1,8500.00,8500.00),
(2,6,1,18000.00,18000.00),(2,5,1,25000.00,25000.00),(2,1,1,45000.00,45000.00),
(3,2,1,75000.00,75000.00),(3,1,1,45000.00,45000.00),(3,5,3,25000.00,75000.00),(3,6,2,18000.00,36000.00),
(4,1,1,45000.00,45000.00),(4,3,1,8500.00,8500.00),
(5,4,1,12000.00,12000.00),(5,5,1,25000.00,25000.00);

COMMIT;
