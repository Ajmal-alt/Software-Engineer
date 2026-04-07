-- ============================================================
-- SOFTWARE PERSONNEL MANAGEMENT SYSTEM (SPMS)
-- Database Schema + Seed Data
-- Compatible with MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS spms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spms_db;

-- ─────────────────────────────────────────────────────────────
-- TABLE: departments
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS departments (
    department_id   INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    department_head VARCHAR(100),
    location        VARCHAR(100),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: users
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    role        ENUM('ADMIN','HR','PROJECT_MANAGER','EMPLOYEE') NOT NULL,
    last_login  DATETIME,
    status      BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: employees
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS employees (
    employee_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT UNIQUE,
    employee_code   VARCHAR(20) UNIQUE NOT NULL,
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    date_of_birth   DATE,
    gender          ENUM('MALE','FEMALE','OTHER'),
    phone           VARCHAR(15),
    address         TEXT,
    city            VARCHAR(50),
    state           VARCHAR(50),
    pincode         VARCHAR(10),
    date_of_joining DATE,
    designation     VARCHAR(100),
    department_id   INT,
    qualification   VARCHAR(100),
    bank_account_no VARCHAR(30),
    ifsc_code       VARCHAR(20),
    pan_card        VARCHAR(20),
    uan_number      VARCHAR(30),
    status          ENUM('ACTIVE','INACTIVE','ON_LEAVE','NOTICE_PERIOD','EX_EMPLOYEE') DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id)       REFERENCES users(user_id)             ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: projects
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS projects (
    project_id   INT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(200) NOT NULL,
    description  TEXT,
    start_date   DATE,
    end_date     DATE,
    status       ENUM('ACTIVE','COMPLETED','ON_HOLD') DEFAULT 'ACTIVE',
    budget       DECIMAL(12,2),
    manager_id   INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES employees(employee_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: project_assignments
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_assignments (
    assignment_id      INT AUTO_INCREMENT PRIMARY KEY,
    project_id         INT NOT NULL,
    employee_id        INT NOT NULL,
    role               VARCHAR(100),
    assigned_date      DATE,
    released_date      DATE,
    allocation_percent INT DEFAULT 100,
    FOREIGN KEY (project_id)  REFERENCES projects(project_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: attendance
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id   INT AUTO_INCREMENT PRIMARY KEY,
    employee_id     INT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time   TIME,
    check_out_time  TIME,
    total_hours     DECIMAL(5,2),
    status          ENUM('PRESENT','ABSENT','HALF_DAY','HOLIDAY','LEAVE') DEFAULT 'PRESENT',
    remarks         VARCHAR(255),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    UNIQUE KEY unique_attendance (employee_id, attendance_date)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: leave_types
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS leave_types (
    leave_type_id   INT AUTO_INCREMENT PRIMARY KEY,
    leave_type_name VARCHAR(50) NOT NULL,
    description     VARCHAR(255),
    max_days_per_year INT
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: leave_applications
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS leave_applications (
    leave_id      INT AUTO_INCREMENT PRIMARY KEY,
    employee_id   INT NOT NULL,
    leave_type_id INT NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE NOT NULL,
    total_days    INT,
    reason        TEXT,
    status        ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
    applied_on    DATETIME DEFAULT CURRENT_TIMESTAMP,
    approved_by   INT,
    approved_on   DATETIME,
    comments      VARCHAR(255),
    FOREIGN KEY (employee_id)   REFERENCES employees(employee_id),
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(leave_type_id),
    FOREIGN KEY (approved_by)   REFERENCES employees(employee_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: leave_balance
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS leave_balance (
    balance_id       INT AUTO_INCREMENT PRIMARY KEY,
    employee_id      INT NOT NULL,
    leave_type_id    INT NOT NULL,
    year             INT NOT NULL,
    total_leaves     INT,
    used_leaves      INT DEFAULT 0,
    remaining_leaves INT,
    FOREIGN KEY (employee_id)   REFERENCES employees(employee_id),
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(leave_type_id),
    UNIQUE KEY unique_balance (employee_id, leave_type_id, year)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: salary_structure
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS salary_structure (
    salary_id          INT AUTO_INCREMENT PRIMARY KEY,
    employee_id        INT NOT NULL,
    basic              DECIMAL(10,2) NOT NULL,
    hra                DECIMAL(10,2) DEFAULT 0,
    conveyance         DECIMAL(10,2) DEFAULT 0,
    medical            DECIMAL(10,2) DEFAULT 0,
    special_allowance  DECIMAL(10,2) DEFAULT 0,
    pf                 DECIMAL(10,2) DEFAULT 0,
    professional_tax   DECIMAL(10,2) DEFAULT 0,
    income_tax         DECIMAL(10,2) DEFAULT 0,
    insurance          DECIMAL(10,2) DEFAULT 0,
    effective_from     DATE NOT NULL,
    effective_to       DATE,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: payroll
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payroll (
    payroll_id       INT AUTO_INCREMENT PRIMARY KEY,
    employee_id      INT NOT NULL,
    month            INT NOT NULL,
    year             INT NOT NULL,
    working_days     INT DEFAULT 0,
    present_days     INT DEFAULT 0,
    gross_salary     DECIMAL(10,2),
    total_deductions DECIMAL(10,2),
    net_salary       DECIMAL(10,2),
    payment_date     DATE,
    status           ENUM('PROCESSED','PENDING','CANCELLED') DEFAULT 'PENDING',
    processed_by     INT,
    FOREIGN KEY (employee_id)  REFERENCES employees(employee_id),
    FOREIGN KEY (processed_by) REFERENCES users(user_id),
    UNIQUE KEY unique_payroll (employee_id, month, year)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: performance_reviews
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS performance_reviews (
    review_id     INT AUTO_INCREMENT PRIMARY KEY,
    employee_id   INT NOT NULL,
    reviewer_id   INT NOT NULL,
    review_period VARCHAR(50),
    review_date   DATE,
    rating        DECIMAL(3,2),
    comments      TEXT,
    status        ENUM('DRAFT','SUBMITTED','APPROVED') DEFAULT 'DRAFT',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (reviewer_id) REFERENCES employees(employee_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: goals
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS goals (
    goal_id       INT AUTO_INCREMENT PRIMARY KEY,
    review_id     INT NOT NULL,
    description   TEXT,
    target_date   DATE,
    weightage     INT DEFAULT 10,
    achieved      BOOLEAN DEFAULT FALSE,
    achieved_date DATE,
    comments      TEXT,
    FOREIGN KEY (review_id) REFERENCES performance_reviews(review_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: system_logs
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS system_logs (
    log_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT,
    action     VARCHAR(200),
    details    TEXT,
    log_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50)
);

-- ============================================================
-- SEED DATA
-- ============================================================

-- Departments
INSERT INTO departments (department_name, department_head, location) VALUES
('Engineering',     'Alice Johnson',  'Building A'),
('Human Resources', 'Bob Smith',      'Building B'),
('Project Management','Carol White',  'Building B'),
('Quality Assurance','David Brown',   'Building A');

-- Leave Types
INSERT INTO leave_types (leave_type_name, description, max_days_per_year) VALUES
('Sick Leave',   'Medical sick leave',          12),
('Casual Leave', 'Personal casual leave',       12),
('Earned Leave', 'Accumulated earned leave',    15),
('Maternity',    'Maternity/Paternity leave',   90);

-- Default Users  (passwords stored as plaintext for demo — see README)
INSERT INTO users (username, password, email, role) VALUES
('admin',   'Admin@123',   'admin@spms.com',      'ADMIN'),
('hrmanager','Hr@123',     'hr@spms.com',         'HR'),
('pm1',     'Pm@123',      'pm1@spms.com',        'PROJECT_MANAGER'),
('emp1',    'Emp@123',     'emp1@spms.com',       'EMPLOYEE'),
('emp2',    'Emp@123',     'emp2@spms.com',       'EMPLOYEE');

-- Employees
INSERT INTO employees (user_id, employee_code, first_name, last_name,
    date_of_birth, gender, phone, address, city, state, pincode,
    date_of_joining, designation, department_id, qualification,
    bank_account_no, ifsc_code, pan_card, status) VALUES
(3, 'EMP-001', 'Carol', 'White',  '1985-03-15', 'FEMALE', '9876500001',
 '10 Oak Street', 'Bangalore', 'Karnataka', '560001',
 '2018-01-10', 'Project Manager', 3, 'B.Tech', '1234567890', 'HDFC0001234', 'ABCPW1234C', 'ACTIVE'),

(4, 'EMP-002', 'David', 'Brown',  '1990-07-22', 'MALE',   '9876500002',
 '22 Pine Avenue','Bangalore', 'Karnataka', '560002',
 '2019-06-01', 'Software Engineer', 1, 'B.E.', '0987654321', 'ICIC0005678', 'DEFDB5678D', 'ACTIVE'),

(5, 'EMP-003', 'Eva',  'Green',   '1992-11-05', 'FEMALE', '9876500003',
 '5 Maple Road',  'Bangalore', 'Karnataka', '560003',
 '2020-03-15', 'QA Engineer', 4, 'M.Sc', '1122334455', 'SBIN0009012', 'GHIEG9012G', 'ACTIVE');

-- Salary structures
INSERT INTO salary_structure (employee_id, basic, hra, conveyance, medical, special_allowance, pf, professional_tax, income_tax, insurance, effective_from) VALUES
(1, 80000, 32000, 1600, 1250, 10000, 9600, 200, 8000, 500, '2018-01-10'),
(2, 50000, 20000, 1600, 1250,  5000, 6000, 200, 3000, 500, '2019-06-01'),
(3, 45000, 18000, 1600, 1250,  4000, 5400, 200, 2500, 500, '2020-03-15');

-- Leave balances (current year)
INSERT INTO leave_balance (employee_id, leave_type_id, year, total_leaves, used_leaves, remaining_leaves) VALUES
(1, 1, 2026, 12,  2, 10),(1, 2, 2026, 12,  1, 11),(1, 3, 2026, 15,  0, 15),
(2, 1, 2026, 12,  0, 12),(2, 2, 2026, 12,  3,  9),(2, 3, 2026, 15,  2, 13),
(3, 1, 2026, 12,  1, 11),(3, 2, 2026, 12,  0, 12),(3, 3, 2026, 15,  0, 15);

-- Sample project
INSERT INTO projects (project_name, description, start_date, end_date, status, budget, manager_id)
VALUES ('SPMS Development','Internal HR system build','2025-01-01','2025-12-31','ACTIVE',500000.00,1);

-- Sample assignment
INSERT INTO project_assignments (project_id, employee_id, role, assigned_date, allocation_percent)
VALUES (1, 2, 'Developer', '2025-01-05', 80),
       (1, 3, 'QA Tester', '2025-01-10', 60);

-- Sample attendance (today's check-in already done)
INSERT INTO attendance (employee_id, attendance_date, check_in_time, status)
VALUES (2, CURDATE(), '09:00:00', 'PRESENT'),
       (3, CURDATE(), '09:15:00', 'PRESENT');

COMMIT;
