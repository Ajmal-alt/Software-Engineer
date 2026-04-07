package com.oss.main;

import com.oss.dao.*;
import com.oss.model.*;
import com.oss.util.ConsoleUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class OSSApplication {

    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    private static UserDAO     userDAO;
    private static CustomerDAO customerDAO;
    private static ProductDAO  productDAO;
    private static CartDAO     cartDAO;
    private static OrderDAO    orderDAO;
    private static PaymentDAO  paymentDAO;

    // ─── Startup ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        ConsoleUtil.printHeader("ONLINE SHOPPING SYSTEM (OSS)");
        System.out.println("  Connecting to database...");
        try {
            userDAO     = new UserDAO();
            customerDAO = new CustomerDAO();
            productDAO  = new ProductDAO();
            cartDAO     = new CartDAO();
            orderDAO    = new OrderDAO();
            paymentDAO  = new PaymentDAO();
            ConsoleUtil.printSuccess("Connected successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Database connection failed: " + e.getMessage());
            System.out.println("  Check config/db.properties and ensure MySQL is running.");
            System.exit(1);
        }
        while (true) {
            try {
                if (currentUser == null) showLoginMenu();
                else                     showRoleDashboard();
            } catch (Exception e) {
                ConsoleUtil.printError("Unexpected error: " + e.getMessage());
            }
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────────
    private static void showLoginMenu() {
        ConsoleUtil.printHeader("LOGIN");
        System.out.print("  Username : "); String u = sc.nextLine().trim();
        System.out.print("  Password : "); String p = sc.nextLine().trim();
        currentUser = userDAO.authenticate(u, p);
        if (currentUser == null) {
            ConsoleUtil.printError("Invalid credentials. Try again.");
        } else {
            if ("CUSTOMER".equals(currentUser.getRole())) {
                Customer full = customerDAO.getCustomerByUserId(currentUser.getUserId());
                if (full != null) {
                    Customer c = (Customer) currentUser;
                    c.setCustomerId(full.getCustomerId());
                    c.setCustomerCode(full.getCustomerCode());
                    c.setFirstName(full.getFirstName());
                    c.setLastName(full.getLastName());
                    c.setPhone(full.getPhone());
                    c.setAddress(full.getAddress());
                    c.setCity(full.getCity());
                    c.setState(full.getState());
                    c.setPincode(full.getPincode());
                    c.setLoyaltyPoints(full.getLoyaltyPoints());
                    c.setCustomerStatus(full.getCustomerStatus());
                }
            }
            ConsoleUtil.printSuccess("Welcome " + currentUser.getUsername() + "! Role: " + currentUser.getRole());
            userDAO.logAction(currentUser.getUserId(), "LOGIN", "User logged in");
        }
    }

    private static void showRoleDashboard() {
        switch (currentUser.getRole()) {
            case "ADMIN":    adminMenu();    break;
            case "MANAGER":  managerMenu();  break;
            case "CUSTOMER": customerMenu(); break;
            default: currentUser = null;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void adminMenu() {
        ConsoleUtil.printHeader("ADMIN DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  manageCustomers();     break;
            case 2:  manageCategories();    break;
            case 3:  manageProducts();      break;
            case 4:  viewAllOrders();       break;
            case 5:  updateOrderStatus();   break;
            case 6:  managePayments();      break;
            case 7:  manageInvoices();      break;
            case 8:  salesReports();        break;
            case 9:  manageUsers();         break;
            case 10: viewSystemLogs();      break;
            case 11: logout();             break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MANAGER MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void managerMenu() {
        ConsoleUtil.printHeader("MANAGER DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  manageCustomers();     break;
            case 2:  manageCategories();    break;
            case 3:  manageProducts();      break;
            case 4:  viewAllOrders();       break;
            case 5:  updateOrderStatus();   break;
            case 6:  managePayments();      break;
            case 7:  manageInvoices();      break;
            case 8:  salesReports();        break;
            case 9:  logout();             break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOMER MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void customerMenu() {
        Customer cust = (Customer) currentUser;
        ConsoleUtil.printHeader("CUSTOMER DASHBOARD");
        cust.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  browseProducts();           break;
            case 2:  searchProduct();            break;
            case 3:  addToCart(cust);            break;
            case 4:  viewCart(cust);             break;
            case 5:  placeOrder(cust);           break;
            case 6:  viewMyOrders(cust);         break;
            case 7:  trackOrder(cust);           break;
            case 8:  viewMyInvoices(cust);       break;
            case 9:  viewMyProfile(cust);        break;
            case 10: logout();                  break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOMER — SELF SERVICE
    // ══════════════════════════════════════════════════════════════════════════
    private static void browseProducts() {
        ConsoleUtil.printSection("BROWSE PRODUCTS");
        System.out.println("  1. All Products");
        System.out.println("  2. By Category");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            printProductCatalogue(productDAO.getActiveProducts());
        } else if (ch == 2) {
            printAllCategories();
            System.out.print("\n  Category ID: "); int catId = readInt();
            printProductCatalogue(productDAO.getProductsByCategory(catId));
        }
    }

    private static void printProductCatalogue(List<Product> list) {
        if (list.isEmpty()) { System.out.println("  No products found."); return; }
        System.out.printf("  %-5s %-10s %-32s %-16s %-10s %-8s %-10s %-8s%n",
                "ID", "Code", "Product Name", "Brand", "Price", "Disc%", "Eff.Price", "Stock");
        System.out.println("  " + "-".repeat(103));
        for (Product p : list)
            System.out.printf("  %-5d %-10s %-32s %-16s %-10.2f %-8.1f %-10.2f %-8d%n",
                    p.getProductId(), p.getProductCode(),
                    p.getProductName().length() > 30 ? p.getProductName().substring(0, 29) + "." : p.getProductName(),
                    nvl(p.getBrand()), p.getUnitPrice(), p.getDiscountPct(),
                    p.getEffectivePrice(), p.getStockQty());
    }

    private static void searchProduct() {
        ConsoleUtil.printSection("SEARCH PRODUCT");
        System.out.print("  Search (name / brand / category): "); String kw = sc.nextLine().trim();
        List<Product> list = productDAO.searchProducts(kw);
        if (list.isEmpty()) System.out.println("  No products matched: " + kw);
        else printProductCatalogue(list);
    }

    private static void addToCart(Customer cust) {
        ConsoleUtil.printSection("ADD TO CART");
        printProductCatalogue(productDAO.getActiveProducts());
        System.out.print("\n  Product ID : "); int pid = readInt();
        Product p = productDAO.getProductById(pid);
        if (p == null || !"ACTIVE".equals(p.getStatus())) {
            ConsoleUtil.printError("Product not found or not available.");
            return;
        }
        System.out.print("  Quantity   : "); int qty = readInt();
        if (qty <= 0) { ConsoleUtil.printError("Invalid quantity."); return; }
        if (qty > p.getStockQty()) {
            ConsoleUtil.printError("Only " + p.getStockQty() + " in stock.");
            return;
        }
        if (cartDAO.addToCart(cust.getCustomerId(), pid, qty)) {
            ConsoleUtil.printSuccess(p.getProductName() + " x" + qty + " added to cart.");
            userDAO.logAction(currentUser.getUserId(), "ADD_TO_CART",
                    "Product=" + p.getProductCode() + " Qty=" + qty);
        } else {
            ConsoleUtil.printError("Failed to add to cart.");
        }
    }

    private static void viewCart(Customer cust) {
        ConsoleUtil.printSection("MY CART");
        List<CartItem> items = cartDAO.getCartByCustomer(cust.getCustomerId());
        if (items.isEmpty()) { System.out.println("  Your cart is empty."); return; }
        printCartTable(items);
        System.out.println("\n  1. Update Quantity  2. Remove Item  3. Clear Cart  4. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            System.out.print("  Cart ID to update  : "); int cid = readInt();
            System.out.print("  New Quantity       : "); int qty = readInt();
            if (cartDAO.updateCartQty(cid, qty)) ConsoleUtil.printSuccess("Quantity updated.");
            else ConsoleUtil.printError("Failed.");
        } else if (ch == 2) {
            System.out.print("  Cart ID to remove  : "); int cid = readInt();
            if (cartDAO.removeFromCart(cid)) ConsoleUtil.printSuccess("Item removed.");
            else ConsoleUtil.printError("Failed.");
        } else if (ch == 3) {
            System.out.print("  Clear entire cart? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                cartDAO.clearCart(cust.getCustomerId());
                ConsoleUtil.printSuccess("Cart cleared.");
            }
        }
    }

    private static void printCartTable(List<CartItem> items) {
        System.out.printf("  %-6s %-32s %-10s %-8s %-8s %-12s%n",
                "CrtID", "Product", "UnitPrice", "Disc%", "Qty", "Line Total");
        System.out.println("  " + "-".repeat(78));
        double grandTotal = 0;
        for (CartItem item : items) {
            System.out.printf("  %-6d %-32s %-10.2f %-8.1f %-8d %-12.2f%n",
                    item.getCartId(),
                    item.getProductName().length() > 30 ? item.getProductName().substring(0, 29) + "." : item.getProductName(),
                    item.getUnitPrice(), item.getDiscountPct(),
                    item.getQuantity(), item.getLineTotal());
            grandTotal += item.getLineTotal();
        }
        System.out.println("  " + "-".repeat(78));
        double shipping = grandTotal > 500 ? 0 : 49;
        System.out.printf("  %-58s Rs. %10.2f%n", "Cart Total (before tax):", grandTotal);
        System.out.printf("  %-58s Rs. %10.2f%n", "Shipping:", shipping);
        ConsoleUtil.printInfo("Shipping is FREE on orders above Rs.500");
    }

    private static void placeOrder(Customer cust) {
        ConsoleUtil.printSection("PLACE ORDER");
        List<CartItem> items = cartDAO.getCartByCustomer(cust.getCustomerId());
        if (items.isEmpty()) {
            ConsoleUtil.printError("Your cart is empty. Add items first.");
            return;
        }
        printCartTable(items);
        System.out.println("\n  Shipping Address:");
        System.out.printf("  Current: %s, %s, %s - %s%n",
                nvl(cust.getAddress()), nvl(cust.getCity()),
                nvl(cust.getState()), nvl(cust.getPincode()));
        System.out.print("  Use above address? (Y/N): ");
        String shippingAddr;
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            shippingAddr = cust.getAddress() + ", " + cust.getCity()
                         + ", " + cust.getState() + " - " + cust.getPincode();
        } else {
            System.out.print("  Enter shipping address: "); shippingAddr = sc.nextLine().trim();
        }
        System.out.print("  Order notes (optional): "); String notes = sc.nextLine().trim();
        System.out.print("  Confirm order? (Y/N)  : ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) return;

        int orderId = orderDAO.placeOrder(cust.getCustomerId(), items,
                shippingAddr, notes, currentUser.getUserId(), customerDAO);
        if (orderId > 0) {
            cartDAO.clearCart(cust.getCustomerId());
            Order o = orderDAO.getOrderById(orderId);
            ConsoleUtil.printSuccess("Order placed! Order Code: " + o.getOrderCode());
            System.out.printf("  Total Amount : Rs. %.2f%n", o.getTotalAmount());
            ConsoleUtil.printInfo("Loyalty points earned: " + (int)(o.getTotalAmount() / 100));
            userDAO.logAction(currentUser.getUserId(), "PLACE_ORDER",
                    "OrderID=" + orderId + " Amount=" + o.getTotalAmount());
            // Offer to pay
            System.out.print("\n  Proceed to payment now? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y"))
                processPaymentForOrder(cust, o);
        } else {
            ConsoleUtil.printError("Failed to place order. Please try again.");
        }
    }

    private static void processPaymentForOrder(Customer cust, Order o) {
        ConsoleUtil.printSection("PAYMENT");
        System.out.printf("  Order   : %s%n", o.getOrderCode());
        System.out.printf("  Amount  : Rs. %.2f%n", o.getTotalAmount());
        System.out.println("  Mode: CREDIT_CARD / DEBIT_CARD / UPI / NET_BANKING / WALLET / CASH_ON_DELIVERY");
        System.out.print("  Payment Mode: "); String mode = sc.nextLine().trim().toUpperCase();
        System.out.print("  Remarks     : "); String remarks = sc.nextLine().trim();
        int payId = paymentDAO.processPayment(o.getOrderId(), cust.getCustomerId(),
                o.getTotalAmount(), mode, remarks, currentUser.getUserId());
        if (payId > 0) {
            ConsoleUtil.printSuccess("Payment successful! Invoice generated automatically.");
            userDAO.logAction(currentUser.getUserId(), "PAYMENT",
                    "OrderID=" + o.getOrderId() + " Mode=" + mode);
            // Print invoice
            List<Invoice> invList = paymentDAO.getInvoicesByCustomer(cust.getCustomerId());
            if (!invList.isEmpty()) {
                paymentDAO.printInvoice(invList.get(0).getInvoiceId());
            }
        } else {
            ConsoleUtil.printError("Payment failed. You can pay later from View My Orders.");
        }
    }

    private static void viewMyOrders(Customer cust) {
        ConsoleUtil.printSection("MY ORDERS");
        List<Order> list = orderDAO.getOrdersByCustomer(cust.getCustomerId());
        if (list.isEmpty()) { System.out.println("  No orders yet."); return; }
        printOrderTable(list);
        System.out.print("\n  View order details? Order ID (0=back): "); int id = readInt();
        if (id > 0) viewOrderDetails(id);
    }

    private static void trackOrder(Customer cust) {
        ConsoleUtil.printSection("TRACK ORDER");
        System.out.print("  Enter Order Code or ID: "); String input = sc.nextLine().trim();
        List<Order> all = orderDAO.getOrdersByCustomer(cust.getCustomerId());
        for (Order o : all) {
            if (o.getOrderCode().equalsIgnoreCase(input)
                    || String.valueOf(o.getOrderId()).equals(input)) {
                System.out.println("\n" + "=".repeat(55));
                System.out.printf("  Order Code   : %s%n", o.getOrderCode());
                System.out.printf("  Order Date   : %s%n", o.getOrderDate());
                System.out.printf("  Total Amount : Rs. %.2f%n", o.getTotalAmount());
                System.out.printf("  Status       : %s%n", o.getStatus());
                System.out.println("  " + "-".repeat(53));
                printOrderStatusTimeline(o.getStatus());
                System.out.println("=".repeat(55));
                return;
            }
        }
        ConsoleUtil.printError("Order not found.");
    }

    private static void printOrderStatusTimeline(String status) {
        String[] stages = {"PENDING","CONFIRMED","PROCESSING","SHIPPED","DELIVERED"};
        int current = -1;
        for (int i = 0; i < stages.length; i++) if (stages[i].equals(status)) { current = i; break; }
        if ("CANCELLED".equals(status) || "RETURNED".equals(status)) {
            System.out.println("  Status: " + status);
            return;
        }
        for (int i = 0; i < stages.length; i++) {
            String mark = i < current ? "[DONE]" : (i == current ? "[HERE]" : "[    ]");
            System.out.println("  " + mark + " " + stages[i]);
        }
    }

    private static void viewMyInvoices(Customer cust) {
        ConsoleUtil.printSection("MY INVOICES");
        List<Invoice> list = paymentDAO.getInvoicesByCustomer(cust.getCustomerId());
        if (list.isEmpty()) { System.out.println("  No invoices found."); return; }
        printInvoiceTable(list);
        System.out.print("\n  Print invoice? Invoice ID (0=back): "); int id = readInt();
        if (id > 0) paymentDAO.printInvoice(id);
    }

    private static void viewMyProfile(Customer cust) {
        ConsoleUtil.printSection("MY PROFILE");
        Customer full = customerDAO.getCustomerById(cust.getCustomerId());
        if (full == null) { ConsoleUtil.printError("Profile not found."); return; }
        System.out.printf("  %-22s: %s%n", "Customer Code",  full.getCustomerCode());
        System.out.printf("  %-22s: %s%n", "Full Name",      full.getFullName());
        System.out.printf("  %-22s: %s%n", "Phone",          nvl(full.getPhone()));
        System.out.printf("  %-22s: %s%n", "Email",          full.getEmail());
        System.out.printf("  %-22s: %s, %s%n", "City/State", nvl(full.getCity()), nvl(full.getState()));
        System.out.printf("  %-22s: %d pts%n", "Loyalty Points", full.getLoyaltyPoints());
        System.out.printf("  %-22s: %s%n", "Status",         full.getCustomerStatus());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOMER MANAGEMENT (Admin/Manager)
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageCustomers() {
        ConsoleUtil.printSection("MANAGE CUSTOMERS");
        System.out.println("  1. Add New Customer");
        System.out.println("  2. View All Customers");
        System.out.println("  3. Update Customer Status");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addCustomer();          break;
            case 2: viewAllCustomers();     break;
            case 3: updateCustomerStatus(); break;
        }
    }

    private static void addCustomer() {
        ConsoleUtil.printSection("ADD NEW CUSTOMER");
        Customer c = new Customer();
        System.out.print("  Username               : "); c.setUsername(sc.nextLine().trim());
        System.out.print("  Password               : "); c.setPassword(sc.nextLine().trim());
        System.out.print("  Email                  : "); c.setEmail(sc.nextLine().trim());
        System.out.print("  Customer Code (CUST-XXX): "); c.setCustomerCode(sc.nextLine().trim());
        System.out.print("  First Name             : "); c.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name              : "); c.setLastName(sc.nextLine().trim());
        System.out.print("  Phone                  : "); c.setPhone(sc.nextLine().trim());
        System.out.print("  Date of Birth (YYYY-MM-DD): "); c.setDateOfBirth(sc.nextLine().trim());
        System.out.print("  Gender (MALE/FEMALE/OTHER): "); c.setGender(sc.nextLine().trim().toUpperCase());
        System.out.print("  Address                : "); c.setAddress(sc.nextLine().trim());
        System.out.print("  City                   : "); c.setCity(sc.nextLine().trim());
        System.out.print("  State                  : "); c.setState(sc.nextLine().trim());
        System.out.print("  Pincode                : "); c.setPincode(sc.nextLine().trim());
        if (customerDAO.addCustomer(c)) {
            ConsoleUtil.printSuccess("Customer added! ID: " + c.getCustomerId());
            userDAO.logAction(currentUser.getUserId(), "ADD_CUSTOMER", "Code=" + c.getCustomerCode());
        } else {
            ConsoleUtil.printError("Failed. Username, email or code may already exist.");
        }
    }

    private static void viewAllCustomers() {
        ConsoleUtil.printSection("ALL CUSTOMERS");
        List<Customer> list = customerDAO.getAllCustomers();
        if (list.isEmpty()) { System.out.println("  No customers found."); return; }
        System.out.printf("  %-5s %-10s %-22s %-15s %-20s %-8s %-10s%n",
                "ID", "Code", "Name", "Phone", "City", "Points", "Status");
        System.out.println("  " + "-".repeat(93));
        for (Customer c : list)
            System.out.printf("  %-5d %-10s %-22s %-15s %-20s %-8d %-10s%n",
                    c.getCustomerId(), c.getCustomerCode(), c.getFullName(),
                    nvl(c.getPhone()), nvl(c.getCity()),
                    c.getLoyaltyPoints(), c.getCustomerStatus());
    }

    private static void updateCustomerStatus() {
        viewAllCustomers();
        System.out.print("\n  Customer ID : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / BLOCKED");
        System.out.print("  New Status  : "); String s = sc.nextLine().trim().toUpperCase();
        if (customerDAO.updateCustomerStatus(id, s))
            ConsoleUtil.printSuccess("Customer status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CATEGORY MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageCategories() {
        ConsoleUtil.printSection("MANAGE CATEGORIES");
        System.out.println("  1. Add Category");
        System.out.println("  2. View All Categories");
        System.out.println("  3. Update Category Status");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addCategory();           break;
            case 2: printAllCategories();    break;
            case 3: updateCategoryStatus();  break;
        }
    }

    private static void addCategory() {
        ConsoleUtil.printSection("ADD CATEGORY");
        Category c = new Category();
        System.out.print("  Category Name : "); c.setCategoryName(sc.nextLine().trim());
        System.out.print("  Description   : "); c.setDescription(sc.nextLine().trim());
        if (productDAO.addCategory(c)) {
            ConsoleUtil.printSuccess("Category added! ID: " + c.getCategoryId());
        } else ConsoleUtil.printError("Failed.");
    }

    private static void printAllCategories() {
        ConsoleUtil.printSection("ALL CATEGORIES");
        List<Category> list = productDAO.getAllCategories();
        if (list.isEmpty()) { System.out.println("  No categories found."); return; }
        System.out.printf("  %-5s %-25s %-35s %-10s %-10s%n",
                "ID", "Category", "Description", "Products", "Status");
        System.out.println("  " + "-".repeat(87));
        for (Category c : list)
            System.out.printf("  %-5d %-25s %-35s %-10d %-10s%n",
                    c.getCategoryId(), c.getCategoryName(),
                    nvl(c.getDescription()), c.getProductCount(), c.getStatus());
    }

    private static void updateCategoryStatus() {
        printAllCategories();
        System.out.print("\n  Category ID : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE");
        System.out.print("  New Status  : "); String s = sc.nextLine().trim().toUpperCase();
        if (productDAO.updateCategoryStatus(id, s))
            ConsoleUtil.printSuccess("Category status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRODUCT MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageProducts() {
        ConsoleUtil.printSection("MANAGE PRODUCTS");
        System.out.println("  1. Add New Product");
        System.out.println("  2. View All Products");
        System.out.println("  3. Update Price");
        System.out.println("  4. Update Discount");
        System.out.println("  5. Update Stock");
        System.out.println("  6. Update Product Status");
        System.out.println("  7. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addProduct();           break;
            case 2: viewAllProductsAdmin(); break;
            case 3: updateProductPrice();   break;
            case 4: updateProductDiscount();break;
            case 5: updateProductStock();   break;
            case 6: updateProductStatus();  break;
        }
    }

    private static void addProduct() {
        ConsoleUtil.printSection("ADD NEW PRODUCT");
        Product p = new Product();
        System.out.print("  Product Code (PRD-XXX) : "); p.setProductCode(sc.nextLine().trim());
        System.out.print("  Product Name           : "); p.setProductName(sc.nextLine().trim());
        printAllCategories();
        System.out.print("  Category ID            : "); p.setCategoryId(readInt());
        System.out.print("  Description            : "); p.setDescription(sc.nextLine().trim());
        System.out.print("  Unit Price (Rs.)       : "); p.setUnitPrice(readDouble());
        System.out.print("  Cost Price (Rs.)       : "); p.setCostPrice(readDouble());
        System.out.print("  Stock Quantity         : "); p.setStockQty(readInt());
        System.out.print("  Unit (e.g. Piece/Pack) : "); p.setUnit(sc.nextLine().trim());
        System.out.print("  Brand                  : "); p.setBrand(sc.nextLine().trim());
        System.out.print("  Tax % (e.g. 18)        : "); p.setTaxPercent(readDouble());
        System.out.print("  Discount % (0=none)    : "); p.setDiscountPct(readDouble());
        if (productDAO.addProduct(p)) {
            ConsoleUtil.printSuccess("Product added! ID: " + p.getProductId());
            userDAO.logAction(currentUser.getUserId(), "ADD_PRODUCT", "Code=" + p.getProductCode());
        } else ConsoleUtil.printError("Failed. Product code may already exist.");
    }

    private static void viewAllProductsAdmin() {
        ConsoleUtil.printSection("ALL PRODUCTS");
        List<Product> list = productDAO.getAllProducts();
        if (list.isEmpty()) { System.out.println("  No products found."); return; }
        System.out.printf("  %-5s %-10s %-30s %-16s %-10s %-6s %-6s %-8s %-10s%n",
                "ID", "Code", "Name", "Category", "Price", "Disc%", "Tax%", "Stock", "Status");
        System.out.println("  " + "-".repeat(103));
        for (Product p : list)
            System.out.printf("  %-5d %-10s %-30s %-16s %-10.2f %-6.1f %-6.1f %-8d %-10s%n",
                    p.getProductId(), p.getProductCode(),
                    p.getProductName().length() > 28 ? p.getProductName().substring(0, 27) + "." : p.getProductName(),
                    p.getCategoryName(), p.getUnitPrice(),
                    p.getDiscountPct(), p.getTaxPercent(),
                    p.getStockQty(), p.getStatus());
    }

    private static void updateProductPrice() {
        viewAllProductsAdmin();
        System.out.print("\n  Product ID : "); int id = readInt();
        System.out.print("  New Price  : "); double price = readDouble();
        if (productDAO.updatePrice(id, price)) ConsoleUtil.printSuccess("Price updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateProductDiscount() {
        viewAllProductsAdmin();
        System.out.print("\n  Product ID     : "); int id = readInt();
        System.out.print("  New Discount % : "); double disc = readDouble();
        if (productDAO.updateDiscount(id, disc)) ConsoleUtil.printSuccess("Discount updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateProductStock() {
        viewAllProductsAdmin();
        System.out.print("\n  Product ID        : "); int id = readInt();
        System.out.print("  Qty to add/deduct : "); int qty = readInt();
        if (productDAO.updateStock(id, qty)) ConsoleUtil.printSuccess("Stock updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateProductStatus() {
        viewAllProductsAdmin();
        System.out.print("\n  Product ID : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / OUT_OF_STOCK");
        System.out.print("  New Status : "); String s = sc.nextLine().trim().toUpperCase();
        if (productDAO.updateProductStatus(id, s)) ConsoleUtil.printSuccess("Product status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ORDER MANAGEMENT (Admin/Manager)
    // ══════════════════════════════════════════════════════════════════════════
    private static void viewAllOrders() {
        ConsoleUtil.printSection("ALL ORDERS");
        List<Order> list = orderDAO.getAllOrders();
        if (list.isEmpty()) { System.out.println("  No orders found."); return; }
        printOrderTable(list);
        System.out.print("\n  View order details? Order ID (0=back): "); int id = readInt();
        if (id > 0) viewOrderDetails(id);
    }

    private static void viewOrderDetails(int orderId) {
        Order o = orderDAO.getOrderById(orderId);
        if (o == null) { ConsoleUtil.printError("Order not found."); return; }
        ConsoleUtil.printSection("ORDER DETAILS — " + o.getOrderCode());
        System.out.printf("  Order Code   : %s%n", o.getOrderCode());
        System.out.printf("  Customer     : %s%n", o.getCustomerName());
        System.out.printf("  Order Date   : %s%n", o.getOrderDate());
        System.out.printf("  Status       : %s%n", o.getStatus());
        System.out.printf("  Address      : %s%n", nvl(o.getShippingAddress()));
        System.out.println("  " + "-".repeat(60));
        List<OrderItem> items = orderDAO.getOrderItems(orderId);
        System.out.printf("  %-30s %-6s %-10s %-8s %-12s%n",
                "Product", "Qty", "UnitPrice", "Disc%", "Line Total");
        System.out.println("  " + "-".repeat(68));
        for (OrderItem item : items)
            System.out.printf("  %-30s %-6d %-10.2f %-8.1f %-12.2f%n",
                    item.getProductName(), item.getQuantity(),
                    item.getUnitPrice(), item.getDiscountPct(), item.getLineTotal());
        System.out.println("  " + "-".repeat(68));
        System.out.printf("  %-46s Subtotal  : Rs. %.2f%n", "", o.getSubtotal());
        System.out.printf("  %-46s Discount  : Rs. %.2f%n", "", o.getDiscountAmount());
        System.out.printf("  %-46s Tax       : Rs. %.2f%n", "", o.getTaxAmount());
        System.out.printf("  %-46s Shipping  : Rs. %.2f%n", "", o.getShippingCharge());
        System.out.printf("  %-46s TOTAL     : Rs. %.2f%n", "", o.getTotalAmount());
    }

    private static void updateOrderStatus() {
        ConsoleUtil.printSection("UPDATE ORDER STATUS");
        viewAllOrders();
        System.out.print("\n  Order ID   : "); int id = readInt();
        System.out.println("  Status: PENDING / CONFIRMED / PROCESSING / SHIPPED / DELIVERED / CANCELLED / RETURNED");
        System.out.print("  New Status : "); String s = sc.nextLine().trim().toUpperCase();
        if (orderDAO.updateOrderStatus(id, s)) {
            ConsoleUtil.printSuccess("Order status updated to " + s);
            userDAO.logAction(currentUser.getUserId(), "UPDATE_ORDER", "OrderID=" + id + " Status=" + s);
        } else ConsoleUtil.printError("Failed.");
    }

    private static void printOrderTable(List<Order> list) {
        if (list.isEmpty()) { System.out.println("  No orders found."); return; }
        System.out.printf("  %-5s %-16s %-22s %-20s %-12s %-12s%n",
                "ID", "Order Code", "Customer", "Order Date", "Total", "Status");
        System.out.println("  " + "-".repeat(90));
        for (Order o : list)
            System.out.printf("  %-5d %-16s %-22s %-20s %-12.2f %-12s%n",
                    o.getOrderId(), o.getOrderCode(), o.getCustomerName(),
                    o.getOrderDate(), o.getTotalAmount(), o.getStatus());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PAYMENT MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void managePayments() {
        ConsoleUtil.printSection("PAYMENT MANAGEMENT");
        System.out.println("  1. View All Payments");
        System.out.println("  2. Process Payment for Order");
        System.out.println("  3. Refund Payment");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: viewAllPayments();   break;
            case 2: processPaymentAdmin(); break;
            case 3: refundPayment();     break;
        }
    }

    private static void viewAllPayments() {
        ConsoleUtil.printSection("ALL PAYMENTS");
        List<Payment> list = paymentDAO.getAllPayments();
        if (list.isEmpty()) { System.out.println("  No payments found."); return; }
        System.out.printf("  %-5s %-18s %-16s %-22s %-12s %-16s %-10s%n",
                "ID", "Ref", "Order", "Customer", "Amount", "Mode", "Status");
        System.out.println("  " + "-".repeat(102));
        for (Payment p : list)
            System.out.printf("  %-5d %-18s %-16s %-22s %-12.2f %-16s %-10s%n",
                    p.getPaymentId(), p.getPaymentRef(), p.getOrderCode(),
                    p.getCustomerName(), p.getAmount(), p.getPaymentMode(), p.getStatus());
    }

    private static void processPaymentAdmin() {
        ConsoleUtil.printSection("PROCESS PAYMENT FOR ORDER");
        viewAllOrders();
        System.out.print("\n  Order ID     : "); int orderId = readInt();
        Order o = orderDAO.getOrderById(orderId);
        if (o == null) { ConsoleUtil.printError("Order not found."); return; }
        System.out.printf("  Amount       : Rs. %.2f%n", o.getTotalAmount());
        System.out.println("  Mode: CREDIT_CARD / DEBIT_CARD / UPI / NET_BANKING / WALLET / CASH_ON_DELIVERY");
        System.out.print("  Payment Mode : "); String mode = sc.nextLine().trim().toUpperCase();
        System.out.print("  Remarks      : "); String remarks = sc.nextLine().trim();
        int payId = paymentDAO.processPayment(orderId, o.getCustomerId(),
                o.getTotalAmount(), mode, remarks, currentUser.getUserId());
        if (payId > 0) {
            ConsoleUtil.printSuccess("Payment processed. Invoice auto-generated.");
            userDAO.logAction(currentUser.getUserId(), "PROCESS_PAYMENT", "OrderID=" + orderId);
        } else ConsoleUtil.printError("Failed.");
    }

    private static void refundPayment() {
        viewAllPayments();
        System.out.print("\n  Payment ID to refund: "); int id = readInt();
        if (paymentDAO.refundPayment(id))
            ConsoleUtil.printSuccess("Payment refunded.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INVOICE MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageInvoices() {
        ConsoleUtil.printSection("INVOICE MANAGEMENT");
        System.out.println("  1. View All Invoices");
        System.out.println("  2. Print Invoice");
        System.out.println("  3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            List<Invoice> list = paymentDAO.getAllInvoices();
            if (list.isEmpty()) { System.out.println("  No invoices found."); return; }
            printInvoiceTable(list);
        } else if (ch == 2) {
            List<Invoice> list = paymentDAO.getAllInvoices();
            printInvoiceTable(list);
            System.out.print("\n  Invoice ID to print: "); int id = readInt();
            paymentDAO.printInvoice(id);
        }
    }

    private static void printInvoiceTable(List<Invoice> list) {
        if (list.isEmpty()) { System.out.println("  No invoices found."); return; }
        System.out.printf("  %-5s %-16s %-16s %-22s %-12s %-10s%n",
                "ID", "Invoice No", "Order Code", "Customer", "Grand Total", "Status");
        System.out.println("  " + "-".repeat(83));
        for (Invoice inv : list)
            System.out.printf("  %-5d %-16s %-16s %-22s %-12.2f %-10s%n",
                    inv.getInvoiceId(), inv.getInvoiceNo(), inv.getOrderCode(),
                    inv.getCustomerName(), inv.getGrandTotal(), inv.getStatus());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void salesReports() {
        ConsoleUtil.printSection("SALES REPORTS");
        System.out.println("  1. Monthly Sales Report");
        System.out.println("  2. Customer Summary");
        System.out.println("  3. Product Catalogue");
        System.out.println("  4. All Orders Summary");
        System.out.println("  5. Payment Summary");
        System.out.println("  6. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: orderDAO.printSalesReport(); break;
            case 2: viewAllCustomers();          break;
            case 3: viewAllProductsAdmin();      break;
            case 4: viewAllOrders();             break;
            case 5: viewAllPayments();           break;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  USER MANAGEMENT (Admin only)
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageUsers() {
        ConsoleUtil.printSection("MANAGE USERS");
        List<User> list = userDAO.getAllUsers();
        System.out.printf("  %-5s %-20s %-30s %-12s %-8s%n",
                "ID", "Username", "Email", "Role", "Active");
        System.out.println("  " + "-".repeat(77));
        for (User u : list)
            System.out.printf("  %-5d %-20s %-30s %-12s %-8s%n",
                    u.getUserId(), u.getUsername(), u.getEmail(),
                    u.getRole(), u.isStatus() ? "Yes" : "No");
        System.out.println("\n  1. Add User   2. Deactivate User   3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            System.out.print("  Username : "); String un = sc.nextLine().trim();
            System.out.print("  Password : "); String pw = sc.nextLine().trim();
            System.out.print("  Email    : "); String em = sc.nextLine().trim();
            System.out.println("  Role: ADMIN / MANAGER / CUSTOMER");
            System.out.print("  Role     : "); String rl = sc.nextLine().trim().toUpperCase();
            if (userDAO.createUser(un, pw, em, rl)) ConsoleUtil.printSuccess("User created.");
            else ConsoleUtil.printError("Failed. Username or email may already exist.");
        } else if (ch == 2) {
            System.out.print("  User ID to deactivate: "); int id = readInt();
            if (userDAO.deactivateUser(id)) ConsoleUtil.printSuccess("User deactivated.");
            else ConsoleUtil.printError("Failed.");
        }
    }

    private static void viewSystemLogs() {
        ConsoleUtil.printSection("SYSTEM LOGS (Last 30)");
        userDAO.printSystemLogs();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private static void logout() {
        userDAO.logAction(currentUser.getUserId(), "LOGOUT", "User logged out");
        ConsoleUtil.printSuccess("Logged out. Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    private static int readInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private static double readDouble() {
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (Exception e) { return 0.0; }
    }

    private static String nvl(String s) { return s == null ? "-" : s; }
}
