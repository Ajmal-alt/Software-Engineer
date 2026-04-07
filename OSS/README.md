# Online Shopping System (OSS)

A fully functional **Java + MySQL console application** developed as part of an Object-Oriented Software Engineering (OOSE) lab practical. Covers the complete online shopping lifecycle — product and category management, cart and order processing, payment recording, and automatic invoice generation.

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
OSS/
├── config/
│   └── db.properties             ← Edit DB password here
├── lib/
│   └── mysql-connector-java.jar  ← Place downloaded JAR here
├── sql/
│   └── oss_schema.sql            ← Full DB schema + seed data
├── src/com/oss/
│   ├── util/
│   │   ├── DBConnection.java     (Singleton pattern)
│   │   └── ConsoleUtil.java
│   ├── model/
│   │   ├── User.java             (Abstract base class)
│   │   ├── Customer.java         (extends User)
│   │   ├── Category.java
│   │   ├── Product.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   └── Invoice.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── CustomerDAO.java
│   │   ├── ProductDAO.java
│   │   ├── CartDAO.java
│   │   ├── OrderDAO.java
│   │   └── PaymentDAO.java
│   └── main/
│       └── OSSApplication.java   ← Entry point
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
| `customer1` | `Cust@123` | Customer (Arjun Sharma) |
| `customer2` | `Cust@123` | Customer (Priya Menon) |
| `customer3` | `Cust@123` | Customer (Rahul Gupta) |

---

## Features by Role

### Customer
| Feature | Description |
|---|---|
| Browse Products | View all active products with effective price after discount |
| Browse by Category | Filter products by category |
| Search Product | Search by name, brand or category keyword |
| Add to Cart | Add products with quantity; updates qty if already in cart |
| View My Cart | See cart with line totals, cart total and shipping estimate |
| Update / Remove Cart | Update quantity or remove individual items |
| Place Order | Checkout from cart with shipping address; earns loyalty points |
| Payment | CREDIT_CARD / DEBIT_CARD / UPI / NET_BANKING / WALLET / COD |
| Track Order | View order status with visual pipeline |
| View My Invoices | See all invoices; print detailed invoice |
| View My Profile | Personal details and loyalty points balance |

### Manager
Everything a Customer sees via the admin panel, plus:
| Feature | Description |
|---|---|
| Manage Customers | Add, view all customers, update status |
| Manage Categories | Add categories, update status |
| Manage Products | Add, update price/discount/stock/status |
| View All Orders | Full order list with item breakdown |
| Update Order Status | PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED |
| Manage Payments | View all payments, process payment for order, refund |
| Manage Invoices | View and print all invoices |
| Sales Reports | Monthly revenue, customer summary, product catalogue |

### Admin
Everything a Manager can do, plus:
| Feature | Description |
|---|---|
| Manage Users | Add new users, deactivate accounts |
| System Logs | Last 30 audit trail entries |

---

## Database Tables (9 Tables)

| Table | Purpose |
|---|---|
| `users` | System login accounts and roles |
| `customers` | Customer profiles with address and loyalty points |
| `categories` | Product category master |
| `products` | Product catalogue with pricing, tax and discount |
| `cart` | Active shopping cart items per customer |
| `orders` | Customer orders with full financial breakdown |
| `order_items` | Line items per order |
| `payments` | Payment records linked to orders |
| `invoices` | Auto-generated invoices on payment |
| `system_logs` | Full audit trail |

---

## Seed Data Included

**6 Categories:** Electronics, Clothing, Home & Kitchen, Books, Sports & Fitness, Beauty & Care

**10 Products** with real-world pricing:
- Samsung Galaxy M34 5G — Rs.18,999 (5% off, 18% tax)
- Apple AirPods Pro — Rs.24,900
- Lenovo IdeaPad Slim 3 — Rs.45,990 (8% off)
- Levi's 511 Slim Jeans — Rs.2,499 (10% off, 12% tax)
- Allen Solly Formal Shirt — Rs.1,299 (15% off)
- Prestige Induction Cooktop — Rs.2,895
- Atomic Habits (book) — Rs.499 (0% tax)
- Boldfit Gym Gloves — Rs.399 (10% off)
- Mamaearth Face Wash — Rs.299 (5% off)
- Sony WH-1000XM5 Headphones — Rs.28,990

**3 Customers** with pre-filled addresses and loyalty points

**4 Orders** across all statuses (PENDING → DELIVERED)

**3 Payments** (UPI, Credit Card, Net Banking) with auto-generated invoices

**Cart items** pre-loaded for customer1 to test checkout immediately

---

## Business Logic Highlights

**Cart Engine:**
- Adding the same product again increments quantity
- Line total = qty × unit_price × (1 − discount%)
- Free shipping on orders above Rs.500, else Rs.49

**Order Placement (Atomic Transaction):**
- Calculates subtotal, discount, tax, shipping and grand total
- Inserts order and all line items
- Deducts stock from each product atomically
- Awards loyalty points (1 pt per Rs.100 spent)
- Clears cart after successful order

**Payment Processing (Atomic Transaction):**
- Records payment with mode and reference
- Updates order status to CONFIRMED
- Auto-generates invoice with full financial summary

**Pricing Formula:**
```
Line Total = (unit_price × qty × (1 − disc%)) × (1 + tax%)
Grand Total = Σ(line totals) + shipping
```

---

## OOSE Lab Coverage (10 Experiments)

| Ex | Topic |
|---|---|
| 1 | System Identification — OSS problem statement and scope |
| 2 | SRS (IEEE 830) — Functional and non-functional requirements |
| 3 | Use Case Model — 3 actors, 28+ use cases |
| 4 | Domain Model & Class Diagram — 9 entity classes |
| 5 | Sequence & Collaboration Diagrams — Place order and payment flows |
| 6 | State Chart & Activity Diagrams — Order lifecycle, cart workflow |
| 7 | Implementation — This Java + MySQL codebase |
| 8 | Testing — Test cases for all use case scenarios |
| 9 | Design Patterns — Singleton, DAO, Abstract Class, Template Method |
| 10 | Modified System & Re-testing |

---

## Design Patterns Applied

| Pattern | Where |
|---|---|
| **Singleton** | `DBConnection` — single shared DB connection |
| **DAO** | All `*DAO` classes separate DB logic from UI |
| **Abstract Class** | `User` with abstract `displayDashboard()` |
| **Inheritance** | `Customer extends User` |
| **Template Method** | `displayDashboard()` overridden per role |
| **Observer (conceptual)** | Payment auto-triggers invoice generation |
| **Strategy (conceptual)** | Cart total calculation encapsulated in `CartDAO` |

---

## Troubleshooting

**Cannot connect to database**
→ MySQL must be running. Check `config\db.properties` password.

**mysql command not found during setup**
→ Add `C:\Program Files\MySQL\MySQL Server 8.0\bin` to Windows PATH.

**Compilation failed**
→ Ensure JDK (not JRE) is installed. Run `javac -version` in cmd.

**Missing JAR error**
→ Ensure `lib\mysql-connector-java.jar` exists with that exact filename.

**Order placement fails**
→ Ensure cart is not empty and all products are still ACTIVE with stock > 0.

**Invoice not generated**
→ Invoice is auto-generated only after a successful payment. Process payment first.

---

*OSS v1.0 — Object-Oriented Software Engineering Lab*
