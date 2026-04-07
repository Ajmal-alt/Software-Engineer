# Exam Registration System (ERS)

A fully functional **Java + MySQL console application** developed as part of an Object-Oriented Software Engineering (OOSE) lab practical. Covers the complete exam lifecycle — student and profile management, exam and schedule management, registration and enrollment, hall ticket generation, result entry, and marksheet generation.

---

## Prerequisites

| Requirement | Version | Download |
|---|---|---|
| Java JDK | 8+ | https://www.oracle.com/java/technologies/downloads/ |
| MySQL Server | 8.0+ | https://dev.mysql.com/downloads/mysql/ |
| MySQL Connector/J | 8.x or 9.x | https://dev.mysql.com/downloads/connector/j/ |

---

## Project Structure

```
ERS/
├── config/
│   └── db.properties             ← Edit DB password here
├── lib/
│   └── mysql-connector-java.jar  ← Place downloaded JAR here
├── sql/
│   └── ers_schema.sql            ← Full DB schema + seed data
├── src/com/ers/
│   ├── util/
│   │   ├── DBConnection.java     (Singleton pattern)
│   │   └── ConsoleUtil.java
│   ├── model/
│   │   ├── User.java             (Abstract base class)
│   │   ├── Student.java          (extends User)
│   │   ├── Subject.java
│   │   ├── Exam.java
│   │   ├── ExamSubject.java
│   │   ├── Registration.java
│   │   └── Result.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── StudentDAO.java
│   │   ├── ExamDAO.java
│   │   ├── RegistrationDAO.java
│   │   └── ResultDAO.java
│   └── main/
│       └── ERSApplication.java   ← Entry point
├── out/                          ← Generated after compile
├── 1_setup_database.bat / .sh
├── 2_compile.bat / .sh
└── 3_run.bat / .sh
```

---

## Quick Start (4 Steps)

### Step 0 — Place MySQL Connector JAR
1. Go to https://dev.mysql.com/downloads/connector/j/
2. Select **Platform Independent → ZIP Archive**
3. Extract → copy the `.jar` into the `lib\` folder
4. Rename to exactly: **`mysql-connector-java.jar`**

### Step 1 — Setup Database
Double-click **`1_setup_database.bat`** → enter MySQL username and password.

### Step 2 — Edit Config
Open **`config\db.properties`** → change `db.password=root` to your MySQL password → Save.

### Step 3 — Compile
Double-click **`2_compile.bat`**

### Step 4 — Run
Double-click **`3_run.bat`** ← use this every time!

---

## Default Login Accounts

| Username | Password | Role | Details |
|---|---|---|---|
| `admin` | `Admin@123` | Admin | Full system access |
| `examiner1` | `Exm@123` | Examiner | Manages exams and results |
| `examiner2` | `Exm@123` | Examiner | Manages exams and results |
| `student1` | `Std@123` | Student | Arjun Kumar — CS Sem 4 |
| `student2` | `Std@123` | Student | Priya Sharma — CS Sem 4 |
| `student3` | `Std@123` | Student | Rahul Verma — EC Sem 2 |
| `student4` | `Std@123` | Student | Neha Singh — CS Sem 6 |

---

## Features by Role

### Student
| Feature | Description |
|---|---|
| View My Profile | Full academic profile — department, course, semester |
| View Available Exams | Exams currently open for registration |
| Register for Exam | Select subjects, view schedule, confirm registration |
| View My Registrations | All registrations with fee and status |
| Pay Exam Fee | Enter payment reference to confirm registration |
| Download Hall Ticket | Print formatted hall ticket with full exam schedule |
| View My Results | Published subject-wise results with grades |
| View Result Summary | Detailed marksheet with overall pass/fail |

### Examiner
| Feature | Description |
|---|---|
| Manage Subjects | Add subjects with code, credits, type |
| Manage Exams | Create exams, update status |
| Manage Exam Schedule | Add subjects to exams with date, time, venue |
| View All Registrations | Browse all student registrations |
| Approve / Cancel Registration | Confirm or cancel a registration |
| Issue Hall Tickets | Issue individually or bulk for an exam |
| Enter Results | Enter marks subject-by-subject per registration |
| Publish Results | Make results visible to students |
| View Results | View by exam, summary report, marksheet |
| Reports | Students, exams, registrations, results |

### Admin
Everything an Examiner can do, plus:
| Feature | Description |
|---|---|
| Manage Students | Add, view, update status, update semester |
| Manage Users | Add users, deactivate accounts |
| System Logs | Last 30 audit trail entries |

---

## Database Tables (9 Tables)

| Table | Purpose |
|---|---|
| `users` | System login accounts and roles |
| `students` | Student academic profiles |
| `subjects` | Subject master with code, credits, type |
| `exams` | Exam events with registration and exam dates |
| `exam_subjects` | Subjects scheduled per exam with date, time, venue |
| `registrations` | Student exam registrations with fee payment |
| `registration_subjects` | Which subjects each student registered for |
| `results` | Marks, grades, pass/fail per subject per student |
| `system_logs` | Full audit trail |

---

## Seed Data Included

**12 Subjects** across Computer Science, Electronics and Mathematics

**4 Exams:**
- Semester 4 CS Examination 2026 — REGISTRATION_OPEN
- Semester 2 Electronics Examination 2026 — REGISTRATION_OPEN
- Semester 6 CS Examination 2026 — REGISTRATION_OPEN
- Supplementary Examination 2025 — COMPLETED

**14 Exam schedule entries** with dates, times and venues

**4 Registrations:**
- STD-001 (Arjun) — 6 subjects, fee paid, hall ticket issued
- STD-002 (Priya) — 4 subjects, fee paid, hall ticket issued
- STD-003 (Rahul) — 3 subjects, fee paid, hall ticket issued
- STD-004 (Neha)  — 3 subjects, fee NOT paid, no hall ticket

**10 Published results** for Sem 4 students with grades

---

## Business Logic Highlights

**Registration (Atomic Transaction):**
- Validates student not already registered for the exam
- Validates subject count does not exceed exam's max_subjects
- Inserts registration and all subject entries atomically
- Status starts as PENDING until fee is paid

**Fee Payment:** Atomically marks fee_paid=TRUE, records reference, sets status=CONFIRMED

**Hall Ticket:** Only issued when fee_paid=TRUE and status=CONFIRMED. Bulk issue available.

**Auto Grade Calculation:**

| Percentage | Grade |
|---|---|
| 90%+ | A+ |
| 80–89% | A |
| 70–79% | B+ |
| 60–69% | B |
| 50–59% | C |
| 40–49% | D |
| Below 40% | F |

**Result Publication:** Results remain hidden from students until published by examiner/admin.

---

## OOSE Lab Coverage (10 Experiments)

| Ex | Topic |
|---|---|
| 1 | System Identification — ERS problem statement and scope |
| 2 | SRS (IEEE 830) — Functional and non-functional requirements |
| 3 | Use Case Model — 3 actors, 30+ use cases |
| 4 | Domain Model & Class Diagram — 7 entity classes |
| 5 | Sequence & Collaboration Diagrams — Registration and result flows |
| 6 | State Chart & Activity Diagrams — Exam lifecycle, registration state |
| 7 | Implementation — This Java + MySQL codebase |
| 8 | Testing — Test cases for all use case scenarios |
| 9 | Design Patterns — Singleton, DAO, Abstract Class, Template Method |
| 10 | Modified System & Re-testing |

---

## Design Patterns Applied

| Pattern | Where |
|---|---|
| **Singleton** | `DBConnection` — single shared DB connection |
| **DAO** | All `*DAO` classes cleanly separate DB from UI |
| **Abstract Class** | `User` with abstract `displayDashboard()` |
| **Inheritance** | `Student extends User` |
| **Template Method** | `displayDashboard()` overridden per role |
| **Strategy (conceptual)** | Grade calculation encapsulated in `ResultDAO` |

---

## Troubleshooting

**Cannot connect to database**
→ MySQL must be running. Check `config\db.properties` password.

**mysql command not found**
→ Add `C:\Program Files\MySQL\MySQL Server 8.0\bin` to Windows PATH.

**Compilation failed**
→ Ensure JDK (not JRE) is installed. Run `javac -version` in cmd.

**Missing JAR error**
→ Ensure `lib\mysql-connector-java.jar` exists with that exact filename.

**Hall ticket cannot be issued**
→ Fee must be paid and registration status must be CONFIRMED first.

**Results not visible to student**
→ Results must be published by examiner/admin before students can view them.

---

*ERS v2.0 — Object-Oriented Software Engineering Lab*
