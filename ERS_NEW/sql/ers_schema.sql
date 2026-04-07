-- ============================================================
-- EXAM REGISTRATION SYSTEM (ERS)
-- Database Schema + Seed Data
-- Compatible with MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS ers_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ers_db;

-- ─────────────────────────────────────────────────────────────
-- TABLE: users
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    role       ENUM('ADMIN','EXAMINER','STUDENT') NOT NULL,
    last_login DATETIME,
    status     BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: students
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    student_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT UNIQUE NOT NULL,
    student_code  VARCHAR(20) UNIQUE NOT NULL,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender        ENUM('MALE','FEMALE','OTHER') DEFAULT 'MALE',
    phone         VARCHAR(15),
    email         VARCHAR(100),
    address       TEXT,
    city          VARCHAR(50),
    state         VARCHAR(50),
    pincode       VARCHAR(10),
    department    VARCHAR(100),
    course        VARCHAR(100),
    semester      INT DEFAULT 1,
    roll_number   VARCHAR(30) UNIQUE,
    year_of_admission INT,
    status        ENUM('ACTIVE','INACTIVE','SUSPENDED') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: subjects
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS subjects (
    subject_id    INT AUTO_INCREMENT PRIMARY KEY,
    subject_code  VARCHAR(20) UNIQUE NOT NULL,
    subject_name  VARCHAR(200) NOT NULL,
    department    VARCHAR(100),
    credits       INT DEFAULT 3,
    subject_type  ENUM('THEORY','PRACTICAL','PROJECT') DEFAULT 'THEORY',
    status        ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: exams
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS exams (
    exam_id         INT AUTO_INCREMENT PRIMARY KEY,
    exam_code       VARCHAR(20) UNIQUE NOT NULL,
    exam_name       VARCHAR(200) NOT NULL,
    exam_type       ENUM('SEMESTER','SUPPLEMENTARY','ENTRANCE','CERTIFICATION') DEFAULT 'SEMESTER',
    academic_year   VARCHAR(10),
    semester        INT,
    department      VARCHAR(100),
    reg_start_date  DATE NOT NULL,
    reg_end_date    DATE NOT NULL,
    exam_start_date DATE NOT NULL,
    exam_end_date   DATE,
    fee_per_subject DECIMAL(8,2) DEFAULT 500.00,
    max_subjects    INT DEFAULT 8,
    status          ENUM('UPCOMING','REGISTRATION_OPEN','REGISTRATION_CLOSED','ONGOING','COMPLETED','CANCELLED') DEFAULT 'UPCOMING',
    created_by      INT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: exam_subjects  (subjects available in an exam)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS exam_subjects (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    exam_id         INT NOT NULL,
    subject_id      INT NOT NULL,
    exam_date       DATE,
    exam_time       TIME,
    duration_mins   INT DEFAULT 180,
    max_marks       INT DEFAULT 100,
    pass_marks      INT DEFAULT 40,
    venue           VARCHAR(200),
    FOREIGN KEY (exam_id)   REFERENCES exams(exam_id),
    FOREIGN KEY (subject_id)REFERENCES subjects(subject_id),
    UNIQUE KEY unique_es (exam_id, subject_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: registrations
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS registrations (
    registration_id  INT AUTO_INCREMENT PRIMARY KEY,
    reg_number       VARCHAR(30) UNIQUE NOT NULL,
    student_id       INT NOT NULL,
    exam_id          INT NOT NULL,
    registered_on    DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_subjects   INT DEFAULT 0,
    total_fee        DECIMAL(10,2) DEFAULT 0.00,
    fee_paid         BOOLEAN DEFAULT FALSE,
    payment_ref      VARCHAR(50),
    payment_date     DATETIME,
    hall_ticket_no   VARCHAR(30) UNIQUE,
    hall_ticket_issued BOOLEAN DEFAULT FALSE,
    status           ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    remarks          TEXT,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (exam_id)    REFERENCES exams(exam_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: registration_subjects  (which subjects a student registered for)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS registration_subjects (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    exam_subject_id INT NOT NULL,
    subject_id      INT NOT NULL,
    fee             DECIMAL(8,2) DEFAULT 500.00,
    FOREIGN KEY (registration_id) REFERENCES registrations(registration_id),
    FOREIGN KEY (exam_subject_id) REFERENCES exam_subjects(id),
    FOREIGN KEY (subject_id)      REFERENCES subjects(subject_id),
    UNIQUE KEY unique_rs (registration_id, subject_id)
);

-- ─────────────────────────────────────────────────────────────
-- TABLE: results
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS results (
    result_id       INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    student_id      INT NOT NULL,
    exam_id         INT NOT NULL,
    subject_id      INT NOT NULL,
    marks_obtained  DECIMAL(6,2),
    max_marks       INT DEFAULT 100,
    pass_marks      INT DEFAULT 40,
    grade           VARCHAR(5),
    result_status   ENUM('PASS','FAIL','ABSENT','WITHHELD') DEFAULT 'ABSENT',
    published       BOOLEAN DEFAULT FALSE,
    entered_by      INT,
    entered_on      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES registrations(registration_id),
    FOREIGN KEY (student_id)      REFERENCES students(student_id),
    FOREIGN KEY (exam_id)         REFERENCES exams(exam_id),
    FOREIGN KEY (subject_id)      REFERENCES subjects(subject_id),
    FOREIGN KEY (entered_by)      REFERENCES users(user_id),
    UNIQUE KEY unique_result (registration_id, subject_id)
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
('admin',     'Admin@123', 'admin@ers.com',     'ADMIN'),
('examiner1', 'Exm@123',   'examiner1@ers.com', 'EXAMINER'),
('examiner2', 'Exm@123',   'examiner2@ers.com', 'EXAMINER'),
('student1',  'Std@123',   'student1@ers.com',  'STUDENT'),
('student2',  'Std@123',   'student2@ers.com',  'STUDENT'),
('student3',  'Std@123',   'student3@ers.com',  'STUDENT'),
('student4',  'Std@123',   'student4@ers.com',  'STUDENT');

-- Students
INSERT INTO students (user_id, student_code, first_name, last_name, date_of_birth, gender,
    phone, email, address, city, state, pincode, department, course,
    semester, roll_number, year_of_admission, status) VALUES
(4, 'STD-001', 'Arjun',  'Kumar',  '2002-06-15', 'MALE',
 '9876300001', 'student1@ers.com', '12 Gandhi Nagar', 'Bangalore', 'Karnataka', '560001',
 'Computer Science', 'B.E. Computer Science', 4, '2022CS001', 2022, 'ACTIVE'),
(5, 'STD-002', 'Priya',  'Sharma', '2002-09-22', 'FEMALE',
 '9876300002', 'student2@ers.com', '45 Nehru Street',  'Chennai',   'Tamil Nadu',  '600001',
 'Computer Science', 'B.E. Computer Science', 4, '2022CS002', 2022, 'ACTIVE'),
(6, 'STD-003', 'Rahul',  'Verma',  '2003-03-10', 'MALE',
 '9876300003', 'student3@ers.com', '7 Rajaji Road',    'Delhi',     'Delhi',       '110001',
 'Electronics',      'B.E. Electronics',      2, '2023EC001', 2023, 'ACTIVE'),
(7, 'STD-004', 'Neha',   'Singh',  '2001-11-05', 'FEMALE',
 '9876300004', 'student4@ers.com', '88 MG Road',       'Pune',      'Maharashtra', '411001',
 'Computer Science', 'B.E. Computer Science', 6, '2021CS001', 2021, 'ACTIVE');

-- Subjects
INSERT INTO subjects (subject_code, subject_name, department, credits, subject_type, status) VALUES
('CS301', 'Data Structures and Algorithms',   'Computer Science', 4, 'THEORY',    'ACTIVE'),
('CS302', 'Database Management Systems',       'Computer Science', 4, 'THEORY',    'ACTIVE'),
('CS303', 'Operating Systems',                 'Computer Science', 4, 'THEORY',    'ACTIVE'),
('CS304', 'Computer Networks',                 'Computer Science', 3, 'THEORY',    'ACTIVE'),
('CS305', 'DBMS Lab',                          'Computer Science', 2, 'PRACTICAL', 'ACTIVE'),
('CS306', 'OS Lab',                            'Computer Science', 2, 'PRACTICAL', 'ACTIVE'),
('EC301', 'Digital Electronics',               'Electronics',      4, 'THEORY',    'ACTIVE'),
('EC302', 'Signals and Systems',               'Electronics',      4, 'THEORY',    'ACTIVE'),
('MA301', 'Engineering Mathematics III',       'Mathematics',      3, 'THEORY',    'ACTIVE'),
('CS501', 'Machine Learning',                  'Computer Science', 4, 'THEORY',    'ACTIVE'),
('CS502', 'Software Engineering',              'Computer Science', 3, 'THEORY',    'ACTIVE'),
('CS503', 'Web Technologies',                  'Computer Science', 3, 'THEORY',    'ACTIVE');

-- Exams
INSERT INTO exams (exam_code, exam_name, exam_type, academic_year, semester, department,
    reg_start_date, reg_end_date, exam_start_date, exam_end_date,
    fee_per_subject, max_subjects, status, created_by) VALUES
('EX-2026-S4', 'Semester 4 Examination 2026', 'SEMESTER',
 '2025-26', 4, 'Computer Science',
 '2026-03-01', '2026-03-31', '2026-04-15', '2026-04-30',
 500.00, 6, 'REGISTRATION_OPEN', 2),
('EX-2026-S2', 'Semester 2 Examination 2026', 'SEMESTER',
 '2025-26', 2, 'Electronics',
 '2026-03-01', '2026-03-31', '2026-04-15', '2026-04-30',
 500.00, 6, 'REGISTRATION_OPEN', 2),
('EX-2026-S6', 'Semester 6 Examination 2026', 'SEMESTER',
 '2025-26', 6, 'Computer Science',
 '2026-03-10', '2026-04-05', '2026-04-20', '2026-05-05',
 500.00, 6, 'REGISTRATION_OPEN', 2),
('EX-2025-SUP', 'Supplementary Examination 2025', 'SUPPLEMENTARY',
 '2024-25', NULL, 'Computer Science',
 '2025-11-01', '2025-11-15', '2025-12-01', '2025-12-15',
 750.00, 4, 'COMPLETED', 2);

-- Exam Subjects (Semester 4 CS exam - exam_id=1)
INSERT INTO exam_subjects (exam_id, subject_id, exam_date, exam_time, duration_mins, max_marks, pass_marks, venue) VALUES
(1, 1, '2026-04-15', '09:00:00', 180, 100, 40, 'Block A - Hall 101'),
(1, 2, '2026-04-17', '09:00:00', 180, 100, 40, 'Block A - Hall 101'),
(1, 3, '2026-04-19', '09:00:00', 180, 100, 40, 'Block A - Hall 102'),
(1, 4, '2026-04-21', '09:00:00', 180, 100, 40, 'Block A - Hall 102'),
(1, 5, '2026-04-23', '02:00:00', 180, 50,  20, 'Lab Block - L101'),
(1, 6, '2026-04-25', '02:00:00', 180, 50,  20, 'Lab Block - L102'),
-- Sem 2 Electronics (exam_id=2)
(2, 7, '2026-04-15', '09:00:00', 180, 100, 40, 'Block B - Hall 201'),
(2, 8, '2026-04-17', '09:00:00', 180, 100, 40, 'Block B - Hall 201'),
(2, 9, '2026-04-19', '09:00:00', 180, 100, 40, 'Block B - Hall 202'),
-- Sem 6 CS (exam_id=3)
(3, 10, '2026-04-20', '09:00:00', 180, 100, 40, 'Block A - Hall 103'),
(3, 11, '2026-04-22', '09:00:00', 180, 100, 40, 'Block A - Hall 103'),
(3, 12, '2026-04-24', '09:00:00', 180, 100, 40, 'Block A - Hall 104'),
-- Supplementary (exam_id=4)
(4, 1, '2025-12-01', '09:00:00', 180, 100, 40, 'Block A - Hall 101'),
(4, 2, '2025-12-03', '09:00:00', 180, 100, 40, 'Block A - Hall 101');

-- Registrations
INSERT INTO registrations (reg_number, student_id, exam_id, registered_on,
    total_subjects, total_fee, fee_paid, payment_ref, payment_date,
    hall_ticket_no, hall_ticket_issued, status) VALUES
('REG-2026-001', 1, 1, '2026-03-05 10:30:00', 6, 3000.00, TRUE,
 'PAY-ERS-001', '2026-03-05 10:35:00', 'HT-2026-001', TRUE, 'CONFIRMED'),
('REG-2026-002', 2, 1, '2026-03-06 11:00:00', 4, 2000.00, TRUE,
 'PAY-ERS-002', '2026-03-06 11:05:00', 'HT-2026-002', TRUE, 'CONFIRMED'),
('REG-2026-003', 3, 2, '2026-03-07 09:00:00', 3, 1500.00, TRUE,
 'PAY-ERS-003', '2026-03-07 09:05:00', 'HT-2026-003', TRUE, 'CONFIRMED'),
('REG-2026-004', 4, 3, '2026-03-11 14:00:00', 3, 1500.00, FALSE,
 NULL, NULL, NULL, FALSE, 'PENDING');

-- Registration Subjects (STD-001 registered for all 6 subjects of exam 1)
INSERT INTO registration_subjects (registration_id, exam_subject_id, subject_id, fee) VALUES
(1,1,1,500),(1,2,2,500),(1,3,3,500),(1,4,4,500),(1,5,5,500),(1,6,6,500),
-- STD-002 registered for 4 subjects
(2,1,1,500),(2,2,2,500),(2,3,3,500),(2,4,4,500),
-- STD-003 registered for 3 subjects (exam 2)
(3,7,7,500),(3,8,8,500),(3,9,9,500),
-- STD-004 registered for 3 subjects (exam 3)
(4,10,10,500),(4,11,11,500),(4,12,12,500);

-- Results (completed supplementary exam + some sem 4 results for demo)
INSERT INTO results (registration_id, student_id, exam_id, subject_id,
    marks_obtained, max_marks, pass_marks, grade, result_status, published, entered_by) VALUES
(1, 1, 1, 1, 78.00, 100, 40, 'B+',  'PASS', TRUE,  2),
(1, 1, 1, 2, 85.00, 100, 40, 'A',   'PASS', TRUE,  2),
(1, 1, 1, 3, 62.00, 100, 40, 'B',   'PASS', TRUE,  2),
(1, 1, 1, 4, 45.00, 100, 40, 'C',   'PASS', TRUE,  2),
(1, 1, 1, 5, 42.00, 50,  20, 'C',   'PASS', TRUE,  2),
(1, 1, 1, 6, 46.00, 50,  20, 'B+',  'PASS', TRUE,  2),
(2, 2, 1, 1, 91.00, 100, 40, 'A+',  'PASS', TRUE,  2),
(2, 2, 1, 2, 88.00, 100, 40, 'A',   'PASS', TRUE,  2),
(2, 2, 1, 3, 35.00, 100, 40, 'F',   'FAIL', TRUE,  2),
(2, 2, 1, 4, 72.00, 100, 40, 'B+',  'PASS', TRUE,  2);

COMMIT;
