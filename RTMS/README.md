# Racing Team Management System (RTMS)

A fully functional **Java + MySQL console application** developed as part of an Object-Oriented Software Engineering (OOSE) lab practical. Covers the complete racing team lifecycle — driver and staff management, race event scheduling, performance and results tracking, sponsorship portfolio, and budget and finance management.

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
RTMS/
├── config/
│   └── db.properties              ← Edit DB password here
├── lib/
│   └── mysql-connector-java.jar   ← Place downloaded JAR here
├── sql/
│   └── rtms_schema.sql            ← Full DB schema + seed data
├── src/com/rtms/
│   ├── util/
│   │   ├── DBConnection.java      (Singleton pattern)
│   │   └── ConsoleUtil.java
│   ├── model/
│   │   ├── User.java              (Abstract base class)
│   │   ├── Driver.java            (extends User)
│   │   ├── Staff.java
│   │   ├── RaceEvent.java
│   │   ├── RaceEntry.java
│   │   ├── Sponsor.java
│   │   └── BudgetTransaction.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── DriverDAO.java
│   │   ├── StaffDAO.java
│   │   ├── RaceEventDAO.java
│   │   ├── SponsorDAO.java
│   │   └── BudgetDAO.java
│   └── main/
│       └── RTMSApplication.java   ← Entry point
├── out/                           ← Generated after compile
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
| `manager1` | `Mgr@123` | Manager | Team operations access |
| `driver1` | `Drv@123` | Driver | Arjun Mehta — SUPERLICENSE, 95 races, 12 wins |
| `driver2` | `Drv@123` | Driver | Ravi Shankar — A Grade, 62 races, 4 wins |
| `driver3` | `Drv@123` | Driver | Priya Kapoor — B Grade, 28 races, 1 win |

---

## Features by Role

### Driver
| Feature | Description |
|---|---|
| View My Profile & Stats | Full profile with career stats — races, wins, podiums, points, win rate |
| View Upcoming Race Events | All scheduled UPCOMING / QUALIFYING / RACE_DAY events |
| View My Race History | Past race entries with qualifying pos, race pos, fastest lap, points |
| Championship Standing | Full driver standings sorted by championship points |
| View My Contract Details | Contract period, annual salary and current status |

### Manager
Everything a Driver sees via the admin panel, plus:
| Feature | Description |
|---|---|
| Manage Drivers | Add driver (creates user + profile), view, update status, update contract |
| Manage Staff | Add staff, view all, filter by department, update status |
| Manage Race Events | Create event, view calendar, update status, enter drivers |
| Record Race Results | Enter qualifying pos, race pos, fastest lap, points, DNF per driver |
| View Race Results | Results by event or driver race history |
| Championship Standings | Ranked table by points with wins and podiums |
| Manage Sponsors | Add sponsor, view portfolio, filter active, summary by type, update status |
| Budget & Finance | Record income/expense, view all transactions, P&L report |
| Reports | Driver roster, staff roster, race calendar, standings, sponsor portfolio, P&L |

### Admin
Everything a Manager can do, plus:
| Feature | Description |
|---|---|
| Manage Users | Add new users, deactivate accounts |
| System Logs | Last 30 audit trail entries |

---

## Database Tables (8 Tables)

| Table | Purpose |
|---|---|
| `users` | System login accounts and roles |
| `drivers` | Driver profiles with career statistics and contract |
| `staff` | Staff profiles across 7 departments |
| `race_events` | Race calendar with circuit and prize money details |
| `race_entries` | Per-driver race results linked to events |
| `sponsors` | Sponsor portfolio with contract values and placement |
| `budget_categories` | 11 income and expense categories |
| `budget_transactions` | All financial transactions with P&L support |
| `system_logs` | Full audit trail |

---

## Seed Data Included

**3 Drivers** with full stats:
- Arjun Mehta — SUPERLICENSE, 95 races, 12 wins, 38 podiums, Rs.85L salary
- Ravi Shankar — A Grade, 62 races, 4 wins, 18 podiums, Rs.55L salary
- Priya Kapoor — B Grade, 28 races, 1 win, 5 podiums, Rs.32L salary

**6 Staff** across Engineering, Strategy, Mechanics, Medical, Logistics departments

**6 Race Events** (F1 and F3 series):
- India Grand Prix 2026 — Buddh International Circuit (COMPLETED)
- Malaysia Grand Prix 2026 — Sepang International Circuit (COMPLETED)
- Singapore Night Race 2026 — Marina Bay Street Circuit (UPCOMING)
- Japanese Grand Prix 2026 — Suzuka Circuit (UPCOMING)
- MRF Challenge Round 1 — Kari Motor Speedway (COMPLETED)
- MRF Challenge Round 2 — Kari Motor Speedway (UPCOMING)

**6 Race Entries** with qualifying times, race positions, fastest laps and points

**6 Sponsors** totalling Rs.145M:
- TechNova Solutions — TITLE, Rs.75M
- SpeedFuel India — PRIMARY, Rs.30M
- Apex Tyres — TECHNICAL, Rs.15M
- Stellar Energy Drinks — SECONDARY, Rs.12M
- DataStream Analytics — ASSOCIATE, Rs.8M
- Heritage Watches — ASSOCIATE, Rs.5M (EXPIRED)

**15 Budget transactions** covering Q1 sponsorship income, prize money, driver/staff salaries, travel, parts and fuel

---

## Business Logic Highlights

**Driver Registration (Atomic Transaction):**
- Creates user account and driver profile in a single transaction
- Rollback on any failure — no orphan user records

**Race Result Recording (Atomic Transaction):**
- Updates race entry with all result data
- Automatically increments driver stats: total_races, total_wins, total_podiums, championship_pts
- Win detected when race_pos = 1; podium when race_pos <= 3

**Championship Standings:**
- Drivers ranked by championship_pts in descending order
- Win rate calculated dynamically: (wins / races) × 100

**P&L Report:**
- Aggregates all income and expense transactions by category
- Displays total income, total expense and net profit/(loss)
- Shows PROFITABLE or LOSS verdict

**Sponsor Portfolio:**
- Sponsors prioritised by type: TITLE → PRIMARY → SECONDARY → TECHNICAL → ASSOCIATE
- Summary shows count and total contract value per tier

---

## OOSE Lab Coverage (10 Experiments)

| Ex | Topic |
|---|---|
| 1 | System Identification — RTMS problem statement and scope |
| 2 | SRS (IEEE 830) — Functional and non-functional requirements |
| 3 | Use Case Model — 3 actors, 28+ use cases |
| 4 | Domain Model & Class Diagram — 7 entity classes |
| 5 | Sequence & Collaboration Diagrams — Record result and budget flows |
| 6 | State Chart & Activity Diagrams — Race event lifecycle, driver status |
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
| **Inheritance** | `Driver extends User` |
| **Template Method** | `displayDashboard()` overridden per role |
| **Strategy (conceptual)** | Championship ranking encapsulated in `DriverDAO` |
| **Facade (conceptual)** | `RTMSApplication` orchestrates all DAOs cleanly |

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

**Race result not updating driver stats**
→ Driver must first be entered for the event using option 5 in Manage Race Events.

**No entries found for event**
→ Use "Enter Driver for Event" to register drivers before recording results.

---

*RTMS v1.0 — Object-Oriented Software Engineering Lab*
