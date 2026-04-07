-- ============================================================
-- RACING TEAM MANAGEMENT SYSTEM (RTMS)
-- Database Schema + Seed Data
-- Compatible with MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS rtms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rtms_db;

-- ─────────────────────────────────────────────────────────────
-- TABLE: users
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    role       ENUM('ADMIN','MANAGER','DRIVER') NOT NULL,
    last_login DATETIME,
    status     BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: drivers
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS drivers (
    driver_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT UNIQUE NOT NULL,
    driver_code      VARCHAR(20) UNIQUE NOT NULL,
    first_name       VARCHAR(50) NOT NULL,
    last_name        VARCHAR(50) NOT NULL,
    date_of_birth    DATE,
    nationality      VARCHAR(50),
    phone            VARCHAR(15),
    email            VARCHAR(100),
    license_number   VARCHAR(30) UNIQUE NOT NULL,
    license_grade    ENUM('A','B','C','SUPERLICENSE') DEFAULT 'B',
    license_expiry   DATE,
    experience_years INT DEFAULT 0,
    total_races      INT DEFAULT 0,
    total_wins       INT DEFAULT 0,
    total_podiums    INT DEFAULT 0,
    championship_pts INT DEFAULT 0,
    contract_start   DATE,
    contract_end     DATE,
    salary           DECIMAL(12,2),
    status           ENUM('ACTIVE','INJURED','SUSPENDED','RETIRED','CONTRACT_ENDED') DEFAULT 'ACTIVE',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: staff
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS staff (
    staff_id         INT AUTO_INCREMENT PRIMARY KEY,
    staff_code       VARCHAR(20) UNIQUE NOT NULL,
    first_name       VARCHAR(50) NOT NULL,
    last_name        VARCHAR(50) NOT NULL,
    role_title       VARCHAR(100),
    department       ENUM('ENGINEERING','STRATEGY','MECHANICS','LOGISTICS','MEDICAL','MEDIA','MANAGEMENT') DEFAULT 'ENGINEERING',
    phone            VARCHAR(15),
    email            VARCHAR(100),
    nationality      VARCHAR(50),
    contract_start   DATE,
    contract_end     DATE,
    salary           DECIMAL(12,2),
    status           ENUM('ACTIVE','ON_LEAVE','SUSPENDED','TERMINATED') DEFAULT 'ACTIVE',
    created_by       INT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: race_events
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS race_events (
    event_id         INT AUTO_INCREMENT PRIMARY KEY,
    event_code       VARCHAR(20) UNIQUE NOT NULL,
    event_name       VARCHAR(200) NOT NULL,
    series           ENUM('F1','F2','F3','GT','RALLY','ENDURANCE','MOTOGP','SUPERBIKE','LOCAL') DEFAULT 'F1',
    circuit_name     VARCHAR(200),
    city             VARCHAR(100),
    country          VARCHAR(100),
    event_date       DATE NOT NULL,
    qualifying_date  DATE,
    practice_date    DATE,
    total_laps       INT,
    circuit_length_km DECIMAL(6,3),
    prize_money      DECIMAL(12,2) DEFAULT 0,
    status           ENUM('UPCOMING','QUALIFYING','RACE_DAY','COMPLETED','CANCELLED','POSTPONED') DEFAULT 'UPCOMING',
    notes            TEXT,
    created_by       INT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: race_entries  (which driver enters which race)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS race_entries (
    entry_id         INT AUTO_INCREMENT PRIMARY KEY,
    event_id         INT NOT NULL,
    driver_id        INT NOT NULL,
    car_number       VARCHAR(10),
    qualifying_pos   INT,
    qualifying_time  VARCHAR(20),
    race_pos         INT,
    race_time        VARCHAR(30),
    fastest_lap      VARCHAR(20),
    laps_completed   INT,
    points_scored    INT DEFAULT 0,
    dnf              BOOLEAN DEFAULT FALSE,
    dnf_reason       VARCHAR(200),
    notes            TEXT,
    FOREIGN KEY (event_id)  REFERENCES race_events(event_id),
    FOREIGN KEY (driver_id) REFERENCES drivers(driver_id),
    UNIQUE KEY unique_entry (event_id, driver_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: sponsors
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sponsors (
    sponsor_id       INT AUTO_INCREMENT PRIMARY KEY,
    sponsor_code     VARCHAR(20) UNIQUE NOT NULL,
    company_name     VARCHAR(200) NOT NULL,
    contact_person   VARCHAR(100),
    contact_phone    VARCHAR(15),
    contact_email    VARCHAR(100),
    industry         VARCHAR(100),
    sponsor_type     ENUM('TITLE','PRIMARY','SECONDARY','TECHNICAL','ASSOCIATE') DEFAULT 'ASSOCIATE',
    contract_value   DECIMAL(14,2),
    contract_start   DATE,
    contract_end     DATE,
    logo_placement   VARCHAR(255),
    status           ENUM('ACTIVE','INACTIVE','NEGOTIATING','EXPIRED') DEFAULT 'ACTIVE',
    created_by       INT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: budget_categories
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS budget_categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description   VARCHAR(255),
    budget_type   ENUM('INCOME','EXPENSE') NOT NULL
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: budget_transactions
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS budget_transactions (
    txn_id        INT AUTO_INCREMENT PRIMARY KEY,
    txn_ref       VARCHAR(30) UNIQUE NOT NULL,
    category_id   INT NOT NULL,
    txn_type      ENUM('INCOME','EXPENSE') NOT NULL,
    amount        DECIMAL(14,2) NOT NULL,
    description   VARCHAR(255),
    txn_date      DATE NOT NULL,
    event_id      INT,
    sponsor_id    INT,
    recorded_by   INT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id)  REFERENCES budget_categories(category_id),
    FOREIGN KEY (event_id)     REFERENCES race_events(event_id),
    FOREIGN KEY (sponsor_id)   REFERENCES sponsors(sponsor_id),
    FOREIGN KEY (recorded_by)  REFERENCES users(user_id)
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
('admin',    'Admin@123', 'admin@rtms.com',   'ADMIN'),
('manager1', 'Mgr@123',   'manager1@rtms.com','MANAGER'),
('driver1',  'Drv@123',   'driver1@rtms.com', 'DRIVER'),
('driver2',  'Drv@123',   'driver2@rtms.com', 'DRIVER'),
('driver3',  'Drv@123',   'driver3@rtms.com', 'DRIVER');

-- Drivers
INSERT INTO drivers (user_id, driver_code, first_name, last_name, date_of_birth, nationality,
    phone, email, license_number, license_grade, license_expiry, experience_years,
    total_races, total_wins, total_podiums, championship_pts,
    contract_start, contract_end, salary, status) VALUES
(3, 'DRV-001', 'Arjun',   'Mehta',   '1995-03-12', 'Indian',
 '9876400001', 'driver1@rtms.com', 'IND-SL-001', 'SUPERLICENSE', '2027-03-31',
 8, 95, 12, 38, 245, '2024-01-01', '2026-12-31', 8500000.00, 'ACTIVE'),
(4, 'DRV-002', 'Ravi',    'Shankar', '1998-07-25', 'Indian',
 '9876400002', 'driver2@rtms.com', 'IND-A-002',  'A',           '2026-07-31',
 5, 62, 4,  18, 142, '2024-01-01', '2026-12-31', 5500000.00, 'ACTIVE'),
(5, 'DRV-003', 'Priya',   'Kapoor',  '2000-11-08', 'Indian',
 '9876400003', 'driver3@rtms.com', 'IND-B-003',  'B',           '2026-11-30',
 3, 28, 1,  5,  68,  '2025-01-01', '2026-12-31', 3200000.00, 'ACTIVE');

-- Staff
INSERT INTO staff (staff_code, first_name, last_name, role_title, department,
    phone, email, nationality, contract_start, contract_end, salary, status, created_by) VALUES
('STF-001', 'Rajesh',  'Kumar',    'Chief Engineer',         'ENGINEERING', '9876500001', 'rajesh@rtms.com',   'Indian',   '2023-01-01', '2026-12-31', 6000000.00, 'ACTIVE', 2),
('STF-002', 'Sunita',  'Verma',    'Race Strategist',        'STRATEGY',    '9876500002', 'sunita@rtms.com',   'Indian',   '2023-01-01', '2026-12-31', 4500000.00, 'ACTIVE', 2),
('STF-003', 'Marco',   'Rossi',    'Head Mechanic',          'MECHANICS',   '9876500003', 'marco@rtms.com',    'Italian',  '2024-01-01', '2026-12-31', 3800000.00, 'ACTIVE', 2),
('STF-004', 'Aisha',   'Patel',    'Team Doctor',            'MEDICAL',     '9876500004', 'aisha@rtms.com',    'Indian',   '2023-06-01', '2026-05-31', 3200000.00, 'ACTIVE', 2),
('STF-005', 'Kiran',   'Singh',    'Logistics Manager',      'LOGISTICS',   '9876500005', 'kiran@rtms.com',    'Indian',   '2024-01-01', '2026-12-31', 2800000.00, 'ACTIVE', 2),
('STF-006', 'David',   'Chen',     'Data Analyst',           'ENGINEERING', '9876500006', 'david@rtms.com',    'British',  '2025-01-01', '2026-12-31', 3500000.00, 'ACTIVE', 2);

-- Race Events
INSERT INTO race_events (event_code, event_name, series, circuit_name, city, country,
    event_date, qualifying_date, practice_date, total_laps, circuit_length_km,
    prize_money, status, created_by) VALUES
('RE-2026-001', 'India Grand Prix 2026',         'F1', 'Buddh International Circuit',    'Greater Noida', 'India',        '2026-02-15', '2026-02-14', '2026-02-13', 60, 5.125, 5000000.00, 'COMPLETED', 2),
('RE-2026-002', 'Malaysia Grand Prix 2026',      'F1', 'Sepang International Circuit',   'Kuala Lumpur',  'Malaysia',     '2026-03-08', '2026-03-07', '2026-03-06', 56, 5.543, 5000000.00, 'COMPLETED', 2),
('RE-2026-003', 'Singapore Night Race 2026',     'F1', 'Marina Bay Street Circuit',      'Singapore',     'Singapore',    '2026-04-05', '2026-04-04', '2026-04-03', 61, 5.063, 5500000.00, 'UPCOMING',  2),
('RE-2026-004', 'Japanese Grand Prix 2026',      'F1', 'Suzuka Circuit',                 'Suzuka',        'Japan',        '2026-04-26', '2026-04-25', '2026-04-24', 53, 5.807, 5000000.00, 'UPCOMING',  2),
('RE-2026-005', 'MRF Challenge Round 1',         'F3', 'Kari Motor Speedway',            'Coimbatore',    'India',        '2026-03-20', '2026-03-20', '2026-03-19', 25, 2.100,  500000.00, 'COMPLETED', 2),
('RE-2026-006', 'MRF Challenge Round 2',         'F3', 'Kari Motor Speedway',            'Coimbatore',    'India',        '2026-05-15', '2026-05-15', '2026-05-14', 25, 2.100,  500000.00, 'UPCOMING',  2);

-- Race Entries (completed races)
INSERT INTO race_entries (event_id, driver_id, car_number, qualifying_pos, qualifying_time,
    race_pos, race_time, fastest_lap, laps_completed, points_scored, dnf) VALUES
-- India GP (event 1)
(1, 1, '7',  3, '1:24.521', 2,  '1:28:45.312', '1:25.112', 60, 18, FALSE),
(1, 2, '17', 8, '1:25.103', 6,  '1:29:12.445', '1:26.034', 60, 8,  FALSE),
-- Malaysia GP (event 2)
(2, 1, '7',  1, '1:32.204', 1,  '1:41:22.511', '1:33.001', 56, 25, FALSE),
(2, 2, '17', 5, '1:33.118', 4,  '1:41:58.223', '1:33.445', 56, 12, FALSE),
(2, 3, '33', 12,'1:34.502', NULL,NULL,           NULL,       22, 0,  TRUE),
-- MRF Round 1 (event 5)
(5, 3, '33', 4, '0:58.312', 3,  '24:12.445',   '0:58.901', 25, 15, FALSE);

-- Update driver totals (championship points reflect seed data)
UPDATE drivers SET championship_pts = 43 WHERE driver_id = 1;
UPDATE drivers SET championship_pts = 20 WHERE driver_id = 2;
UPDATE drivers SET championship_pts = 15 WHERE driver_id = 3;

-- Sponsors
INSERT INTO sponsors (sponsor_code, company_name, contact_person, contact_phone,
    contact_email, industry, sponsor_type, contract_value,
    contract_start, contract_end, logo_placement, status, created_by) VALUES
('SP-001', 'TechNova Solutions',    'Vikram Nair',   '9800100001', 'vikram@technova.com',  'Technology',   'TITLE',     75000000.00, '2025-01-01', '2026-12-31', 'Car livery, driver suit, pit wall',     'ACTIVE', 2),
('SP-002', 'SpeedFuel India',       'Anand Gupta',   '9800100002', 'anand@speedfuel.in',   'Energy',       'PRIMARY',   30000000.00, '2025-01-01', '2026-12-31', 'Nose cone, sidepods',                   'ACTIVE', 2),
('SP-003', 'Apex Tyres',            'Ritu Sharma',   '9800100003', 'ritu@apextyres.com',   'Automotive',   'TECHNICAL', 15000000.00, '2024-06-01', '2026-05-31', 'Wheels, technical partner branding',    'ACTIVE', 2),
('SP-004', 'Stellar Energy Drinks', 'Marco Singh',   '9800100004', 'marco@stellar.com',    'FMCG',         'SECONDARY', 12000000.00, '2025-03-01', '2026-02-28', 'Helmet, rear wing',                     'ACTIVE', 2),
('SP-005', 'Heritage Watches',      'Pooja Mehta',   '9800100005', 'pooja@heritage.com',   'Luxury',       'ASSOCIATE',  5000000.00, '2025-01-01', '2025-12-31', 'Driver suit sleeve',                    'EXPIRED',2),
('SP-006', 'DataStream Analytics',  'James Parker',  '9800100006', 'james@datastream.com', 'Technology',   'ASSOCIATE',  8000000.00, '2026-01-01', '2027-12-31', 'Helmet visor, social media',            'ACTIVE', 2);

-- Budget Categories
INSERT INTO budget_categories (category_name, description, budget_type) VALUES
('Sponsorship Income',   'Revenue from sponsors',                    'INCOME'),
('Prize Money',          'Race prize money',                         'INCOME'),
('Driver Salary',        'Driver contracts and salaries',            'EXPENSE'),
('Staff Salary',         'Staff wages and bonuses',                  'EXPENSE'),
('Race Entry Fees',      'Entry fees for race events',               'EXPENSE'),
('Travel & Logistics',   'Flights, hotels, freight',                 'EXPENSE'),
('Equipment & Parts',    'Car parts, tools, equipment',              'EXPENSE'),
('Fuel & Consumables',   'Fuel, tyres, lubricants',                  'EXPENSE'),
('Marketing & Media',    'PR, photography, branding',                'EXPENSE'),
('Medical & Safety',     'Medical facilities, safety gear',          'EXPENSE'),
('Miscellaneous Income', 'Other income sources',                     'INCOME');

-- Budget Transactions
INSERT INTO budget_transactions (txn_ref, category_id, txn_type, amount, description, txn_date, event_id, sponsor_id, recorded_by) VALUES
('TXN-2026-001', 1, 'INCOME',   37500000.00, 'TechNova Q1 sponsorship payment',       '2026-01-15', NULL, 1, 2),
('TXN-2026-002', 1, 'INCOME',   15000000.00, 'SpeedFuel Q1 payment',                  '2026-01-20', NULL, 2, 2),
('TXN-2026-003', 1, 'INCOME',    7500000.00, 'Apex Tyres H1 payment',                 '2026-01-25', NULL, 3, 2),
('TXN-2026-004', 2, 'INCOME',    5000000.00, 'India GP prize money',                  '2026-02-20', 1,    NULL, 2),
('TXN-2026-005', 2, 'INCOME',    5000000.00, 'Malaysia GP prize money',               '2026-03-12', 2,    NULL, 2),
('TXN-2026-006', 3, 'EXPENSE',   4250000.00, 'Driver salaries Q1',                    '2026-01-31', NULL, NULL, 2),
('TXN-2026-007', 4, 'EXPENSE',   5025000.00, 'Staff salaries Q1',                     '2026-01-31', NULL, NULL, 2),
('TXN-2026-008', 5, 'EXPENSE',    800000.00, 'India & Malaysia GP entry fees',        '2026-01-10', NULL, NULL, 2),
('TXN-2026-009', 6, 'EXPENSE',   3200000.00, 'India GP travel and logistics',         '2026-02-10', 1,    NULL, 2),
('TXN-2026-010', 6, 'EXPENSE',   2800000.00, 'Malaysia GP travel and logistics',      '2026-03-01', 2,    NULL, 2),
('TXN-2026-011', 7, 'EXPENSE',   8500000.00, 'Car parts and upgrades Q1',             '2026-02-05', NULL, NULL, 2),
('TXN-2026-012', 8, 'EXPENSE',   1200000.00, 'Fuel, tyres and consumables India GP',  '2026-02-15', 1,    NULL, 2),
('TXN-2026-013', 8, 'EXPENSE',   1350000.00, 'Fuel, tyres and consumables Malaysia GP','2026-03-08',2,    NULL, 2),
('TXN-2026-014', 9, 'EXPENSE',    650000.00, 'Marketing and media Q1',                '2026-03-01', NULL, NULL, 2),
('TXN-2026-015',11, 'INCOME',     500000.00, 'Merchandise sales Q1',                  '2026-03-31', NULL, NULL, 2);

COMMIT;
