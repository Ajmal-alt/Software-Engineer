# Marketing Management System (MMS)

A fully functional **Java + MySQL console application** developed as part of an Object-Oriented Software Engineering (OOSE) lab practical. Covers the complete marketing lifecycle — lead capture, pipeline management, customer conversion, product catalogue, promotions, and order processing with automatic discount calculation.

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
MMS/
├── config/
│   └── db.properties             ← Edit DB password here
├── lib/
│   └── mysql-connector-java.jar  ← Place downloaded JAR here
├── sql/
│   └── mms_schema.sql            ← Full DB schema + seed data
├── src/com/mms/
│   ├── util/
│   │   ├── DBConnection.java     (Singleton pattern)
│   │   └── ConsoleUtil.java
│   ├── model/
│   │   ├── User.java             (Abstract base class)
│   │   ├── Lead.java
│   │   ├── Customer.java
│   │   ├── Product.java
│   │   ├── Promotion.java
│   │   ├── Order.java
│   │   └── LeadActivity.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── LeadDAO.java
│   │   ├── CustomerDAO.java
│   │   ├── ProductDAO.java
│   │   ├── PromotionDAO.java
│   │   └── OrderDAO.java
│   └── main/
│       └── MMSApplication.java   ← Entry point
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
Double-click **`1_setup_database.bat`** → enter your MySQL username and password.

### Step 2 — Edit Config
Open **`config\db.properties`** → change `db.password=root` to your MySQL password → Save.

### Step 3 — Compile
Double-click **`2_compile.bat`**

### Step 4 — Run
Double-click **`3_run.bat`** ← use this every time!

---

## Default Login Accounts

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | Admin |
| `manager1` | `Mgr@123` | Manager |
| `agent1` | `Agt@123` | Agent |
| `agent2` | `Agt@123` | Agent |

---

## Features by Role

### Agent
| Feature | Description |
|---|---|
| Add Lead | Register new prospect with source, interest area, budget |
| View My Leads | See all leads assigned to this agent |
| Update Lead Status | Move leads through pipeline stages |
| Log Lead Activity | Record calls, emails, demos, proposals with outcome |
| Convert Lead to Customer | Atomically marks lead CONVERTED and creates customer record |
| View Customers | Browse all customers |
| Create Order | Place orders for customers with optional promotion |
| View Products & Promotions | Browse active product catalogue and current promotions |

### Manager
Everything an Agent can do, plus:
| Feature | Description |
|---|---|
| View All Leads | Full lead pipeline across all agents |
| Filter Leads by Status | NEW / CONTACTED / QUALIFIED / PROPOSAL_SENT / NEGOTIATION |
| View Lead Activities | Full activity log per lead |
| Reassign Leads | Transfer leads between agents |
| Add / Update Products | Full product management with price and stock control |
| Add Promotions | Create % / flat / bundle promotions with validity |
| Link Products to Promotions | Associate products with active promotions |
| View All Orders | Complete order history with discount breakdown |
| Reports | Pipeline summary, revenue by month, conversion rate |

### Admin
Everything a Manager can do, plus:
| Feature | Description |
|---|---|
| Manage Users | Add new users, deactivate accounts |
| View System Logs | Last 30 audit trail entries |

---

## Database Tables (9 Tables)

| Table | Purpose |
|---|---|
| `users` | System login accounts and roles |
| `leads` | Prospect records with pipeline status and priority |
| `lead_activities` | Activity log per lead (calls, demos, proposals) |
| `customers` | Converted customers with segment and loyalty points |
| `products` | Product and service catalogue with pricing |
| `promotions` | Discount promotions with validity and usage limits |
| `product_promotions` | Many-to-many link between products and promotions |
| `orders` | Customer orders with discount and final amount |
| `order_items` | Line items per order with quantity and pricing |
| `system_logs` | Full audit trail of all key operations |

---

## Seed Data Included

**7 Leads** across all pipeline stages:
- NEW → CONTACTED → QUALIFIED → PROPOSAL_SENT → NEGOTIATION → CONVERTED → LOST
- Sources: Website, Email Campaign, Referral, Social Media, Cold Call, Event

**4 Customers** with segments:
- 1 converted from lead (Deepika Singh — EduTech Co)
- 3 direct customers (Enterprise, Corporate, SME)

**6 Products:**
- Marketing Analytics Suite — Rs.45,000
- CRM Enterprise Package — Rs.75,000
- Email Campaign Tool — Rs.8,500
- Social Media Manager — Rs.12,000
- Marketing Consulting (1-day) — Rs.25,000
- SEO Optimization Package — Rs.18,000

**5 Promotions:**
- Corporate 20% Off (min Rs.50,000)
- Flat Rs.5,000 Off (min Rs.20,000)
- Analytics Bundle 15% Off
- Q4 Year End 25% Off (expired)
- SME Special 10% Off

**5 Orders** with automatic discount calculation and loyalty point accrual

---

## Business Logic Highlights

**Lead Pipeline:** NEW → CONTACTED → QUALIFIED → PROPOSAL_SENT → NEGOTIATION → CONVERTED / LOST

**Lead Conversion:** Atomically marks lead as CONVERTED and creates customer record in single DB transaction. Rolls back on any failure.

**Order Creation:**
- Select multiple products with quantities
- Auto-calculates total, applies promotion discount (%, flat, bundle)
- Respects min_purchase and max_discount caps
- Deducts stock, increments promo usage_count
- Updates customer total_purchases and loyalty_points (1 pt per Rs.100)

**Promotion Engine:**
- Validates ACTIVE status and date range (CURDATE BETWEEN start and end)
- Validates usage_count < usage_limit before applying
- Supports PERCENTAGE_DISCOUNT, FLAT_DISCOUNT, BUNDLE discount types

---

## OOSE Lab Coverage (10 Experiments)

| Ex | Topic |
|---|---|
| 1 | System Identification — MMS problem statement and scope |
| 2 | SRS (IEEE 830) — Functional and non-functional requirements |
| 3 | Use Case Model — 3 actors, 28 use cases |
| 4 | Domain Model & Class Diagram — 7 entity classes |
| 5 | Sequence & Collaboration Diagrams — Lead conversion and order flows |
| 6 | State Chart & Activity Diagrams — Lead lifecycle, order workflow |
| 7 | Implementation — This Java + MySQL codebase |
| 8 | Testing — Test cases for all use case scenarios |
| 9 | Design Patterns — Singleton, DAO, Abstract Class, Strategy |
| 10 | Modified System & Re-testing |

---

## Design Patterns Applied

| Pattern | Where |
|---|---|
| **Singleton** | `DBConnection` — single shared DB connection instance |
| **DAO** | All `*DAO` classes — DB logic cleanly separated from UI |
| **Abstract Class** | `User` with abstract `displayDashboard()` |
| **Factory-like** | `UserDAO.authenticate()` returns role-based User |
| **Strategy (conceptual)** | `PromotionDAO.calculateDiscount()` encapsulates discount logic |
| **Observer (conceptual)** | Lead conversion atomically triggers customer creation |

---

## Troubleshooting

**Cannot connect to database**
→ MySQL must be running. Check `config\db.properties` password.

**mysql command not found during setup**
→ Add `C:\Program Files\MySQL\MySQL Server 8.0\bin` to Windows PATH.

**Compilation failed at util**
→ Ensure JDK (not JRE) is installed. Run `javac -version` in cmd.

**Missing JAR error**
→ Ensure `lib\mysql-connector-java.jar` exists with that exact filename.

**Order creation fails**
→ Ensure customer exists and at least one product is added before confirming.

**Promotion not applying**
→ Check that the promotion is ACTIVE, not expired, and order total meets min_purchase.

---

*MMS v1.0 — Object-Oriented Software Engineering Lab*
