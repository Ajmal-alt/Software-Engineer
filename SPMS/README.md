# Software Personnel Management System (SPMS)

A fully functional **Java + MySQL console application** developed as part of an Object-Oriented Software Engineering (OOSE) lab practical. It covers the complete HR lifecycle for a software organisation — from employee onboarding to payroll processing.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Quick Start (3 Steps)](#quick-start)
4. [Default Login Accounts](#default-login-accounts)
5. [Features by Role](#features-by-role)
6. [Database Schema Overview](#database-schema-overview)
7. [Configuration](#configuration)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

| Requirement | Minimum Version | Download |
|---|---|---|
| **Java JDK** | 8 (Java SE 8+) | https://www.oracle.com/java/technologies/downloads/ |
| **MySQL Server** | 8.0 | https://dev.mysql.com/downloads/mysql/ |
| **MySQL Connector/J** | 8.x | https://dev.mysql.com/downloads/connector/j/ |

> **Note:** You do NOT need Maven, Gradle, or any IDE. A plain JDK and MySQL are all that's needed.

---

## Project Structure

```
SPMS/
│
├── config/
│   └── db.properties          ← Edit DB credentials here
│
├── lib/
│   └── mysql-connector-java.jar  ← Place the downloaded JAR here
│
├── sql/
│   └── spms_schema.sql        ← Full DB schema + seed data
│
├── src/
│   └── com/spms/
│       ├── util/
│       │   ├── DBConnection.java   (Singleton DB connection)
│       │   └── ConsoleUtil.java    (Formatted console output)
│       ├── model/
│       │   ├── User.java           (Abstract base class)
│       │   ├── Employee.java
│       │   ├── Attendance.java
│       │   ├── LeaveApplication.java
│       │   ├── LeaveBalance.java
│       │   ├── Payroll.java
│       │   ├── Project.java
│       │   ├── Department.java
│       │   └── PerformanceReview.java
│       ├── dao/
│       │   ├── UserDAO.java
│       │   ├── EmployeeDAO.java
│       │   ├── AttendanceDAO.java
│       │   ├── LeaveDAO.java
│       │   ├── PayrollDAO.java
│       │   ├── ProjectDAO.java
│       │   └── PerformanceDAO.java
│       └── main/
│           └── SPMSApplication.java  ← Entry point
│
├── out/                       ← Generated after compilation (auto-created)
│
├── 1_setup_database.bat  /  1_setup_database.sh
├── 2_compile.bat         /  2_compile.sh
└── 3_run.bat             /  3_run.sh
```

---

## Quick Start

### Step 0 — Place the MySQL Connector JAR

1. Go to https://dev.mysql.com/downloads/connector/j/
2. Choose **Platform Independent** → download the `.zip`
3. Extract it and copy `mysql-connector-java-x.x.x.jar` into the `lib/` folder
4. Rename it to exactly: **`mysql-connector-java.jar`**

---

### Step 1 — Set Up the Database

**Windows:**
```
Double-click   1_setup_database.bat
```
Or in Command Prompt:
```cmd
1_setup_database.bat
```

**Linux / macOS:**
```bash
chmod +x 1_setup_database.sh 2_compile.sh 3_run.sh
./1_setup_database.sh
```

This will:
- Create the `spms_db` database
- Create all 13 tables
- Insert seed data (departments, leave types, sample employees, salary structures)

---

### Step 2 — Edit DB Configuration

Open `config/db.properties` in any text editor:

```properties
db.url=jdbc:mysql://localhost:3306/spms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=YOUR_MYSQL_PASSWORD_HERE
```

Change `db.password` to your actual MySQL root password (or whichever user you set up).

---

### Step 3 — Compile

**Windows:**
```
Double-click   2_compile.bat
```

**Linux / macOS:**
```bash
./2_compile.sh
```

---

### Step 4 — Run

**Windows:**
```
Double-click   3_run.bat
```

**Linux / macOS:**
```bash
./3_run.sh
```

The console application will launch and prompt for login.

---

## Default Login Accounts

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | Admin |
| `hrmanager` | `Hr@123` | HR Manager |
| `pm1` | `Pm@123` | Project Manager |
| `emp1` | `Emp@123` | Employee (David Brown) |
| `emp2` | `Emp@123` | Employee (Eva Green) |

> **Security note:** This demo stores passwords in plain text. For production use, replace with BCrypt hashing (e.g., using the `jBCrypt` library).

---

## Features by Role

### Employee
| Feature | Description |
|---|---|
| View Profile | Full personal and professional details |
| Mark Attendance | Check-in and check-out (once per day) |
| Apply for Leave | Sick / Casual / Earned leave with balance check |
| View Leave Balance | Remaining days per leave type |
| View Payslip | Detailed salary breakdown with deductions |
| View Projects | Active project assignments |
| View Performance | Past performance reviews and ratings |

### Project Manager
Everything an Employee can do, plus:

| Feature | Description |
|---|---|
| View Team Attendance | Today's check-in/out for all team members |
| Manage Pending Leaves | Approve or reject leave requests |
| Create Project | New project with budget and dates |
| Assign Employee | Link employees to projects with role/allocation |
| View Team Members | Full team roster per project |
| Update Project Status | ACTIVE / COMPLETED / ON_HOLD |
| Evaluate Performance | Submit ratings (1–5) and comments |

### HR Manager
| Feature | Description |
|---|---|
| Add Employee | Full onboarding — creates user + employee records in one transaction |
| View All Employees | Directory with department and status |
| Deactivate Employee | Mark employee as INACTIVE |
| Process Payroll | Calculates gross, deductions, net for all active employees |
| Generate Reports | Employee directory, project allocation, performance reviews, payroll summary |
| Manage Departments | Add and update departments |

### Admin
| Feature | Description |
|---|---|
| List All Users | Full user table with roles and active status |
| Deactivate User | Disable login for any user |
| View System Logs | Last 30 audit log entries |
| System Configuration | Guide to updating DB settings |
| Manage Departments | Add/update departments |

---

## Database Schema Overview

| Table | Purpose |
|---|---|
| `users` | Login credentials and role |
| `employees` | Full employee profile |
| `departments` | Organisational departments |
| `projects` | Software projects |
| `project_assignments` | Employee ↔ project links |
| `attendance` | Daily check-in/check-out records |
| `leave_types` | Sick / Casual / Earned / Maternity |
| `leave_applications` | Leave requests with approval workflow |
| `leave_balance` | Per-employee, per-year leave quota |
| `salary_structure` | Earnings and deductions per employee |
| `payroll` | Monthly processed payroll records |
| `performance_reviews` | Manager-submitted evaluations |
| `goals` | Goals linked to a performance review |
| `system_logs` | Audit trail of all key actions |

---

## Configuration

All database settings live in `config/db.properties`:

```properties
# JDBC URL — change host/port/database name if non-default
db.url=jdbc:mysql://localhost:3306/spms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# MySQL username
db.username=root

# MySQL password
db.password=root
```

If `db.properties` is missing, the application falls back to `localhost:3306/spms_db` with username `root` / password `root`.

---

## Troubleshooting

### "Cannot connect to database"
- Ensure MySQL Server is **running** (`services.msc` on Windows, `sudo systemctl start mysql` on Linux)
- Verify the password in `config/db.properties`
- Make sure `spms_db` exists (re-run Step 1 if needed)

### "MySQL JDBC Driver not found"
- Ensure `lib/mysql-connector-java.jar` exists and is named exactly that
- If using JDK 11+, prefer the Connector/J 8.x series

### "mysql command not found" during setup
- Add MySQL's `bin` folder to your system PATH
- Windows typical path: `C:\Program Files\MySQL\MySQL Server 8.0\bin`
- Mac with Homebrew: `export PATH="/usr/local/mysql/bin:$PATH"`

### Compilation error on Windows with `^` line continuation
- Use Command Prompt (`cmd.exe`), not PowerShell
- PowerShell uses backtick (`` ` ``) not caret (`^`) for line continuation

### "Access denied for user 'root'"
- Some MySQL installs require `ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'yourpassword';`
- Then `FLUSH PRIVILEGES;`

---

## Design Patterns Used (OOSE Ex 9)

| Pattern | Where Applied |
|---|---|
| **Singleton** | `DBConnection` — one connection instance |
| **DAO (Data Access Object)** | All `*DAO` classes separate DB logic from business logic |
| **Abstract Class / Template** | `User` abstract class with `displayDashboard()` |
| **Factory-like construction** | `UserDAO.authenticate()` returns typed `Employee` or anonymous `User` |
| **Observer (conceptual)** | Leave approval triggers balance deduction atomically via transaction |
| **Strategy (conceptual)** | Payroll calculation is encapsulated in `PayrollDAO.processForEmployee()` |

---

## Academic Context

This project was built across 10 OOSE lab exercises:

| Ex | Topic |
|---|---|
| 1 | System identification & problem statement |
| 2 | SRS documentation (IEEE 830) |
| 3 | Use case modelling |
| 4 | Domain model & class diagram |
| 5 | Sequence & collaboration diagrams |
| 6 | State chart & activity diagrams |
| 7 | **Implementation** (this codebase) |
| 8 | Test cases for all scenarios |
| 9 | Design patterns applied |
| 10 | Modified system & re-testing |

---

*SPMS v1.0 — Object-Oriented Software Engineering Lab*
