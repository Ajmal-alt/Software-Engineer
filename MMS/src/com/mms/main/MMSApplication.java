package com.mms.main;

import com.mms.dao.*;
import com.mms.model.*;
import com.mms.util.ConsoleUtil;

import java.sql.SQLException;
import java.util.*;

public class MMSApplication {

    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    private static UserDAO       userDAO;
    private static LeadDAO       leadDAO;
    private static CustomerDAO   customerDAO;
    private static ProductDAO    productDAO;
    private static PromotionDAO  promoDAO;
    private static OrderDAO      orderDAO;

    // ─── Startup ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        ConsoleUtil.printHeader("MARKETING MANAGEMENT SYSTEM (MMS)");
        System.out.println("  Connecting to database...");
        try {
            userDAO     = new UserDAO();
            leadDAO     = new LeadDAO();
            customerDAO = new CustomerDAO();
            productDAO  = new ProductDAO();
            promoDAO    = new PromotionDAO();
            orderDAO    = new OrderDAO();
            ConsoleUtil.printSuccess("Connected successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Database connection failed: " + e.getMessage());
            System.out.println("  Check config/db.properties and ensure MySQL is running.");
            System.exit(1);
        }
        while (true) {
            try {
                if (currentUser == null) showLoginMenu();
                else showRoleDashboard();
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
        if (currentUser == null) ConsoleUtil.printError("Invalid credentials. Try again.");
        else {
            ConsoleUtil.printSuccess("Welcome " + currentUser.getUsername() + "! Role: " + currentUser.getRole());
            userDAO.logAction(currentUser.getUserId(), "LOGIN", "User logged in");
        }
    }

    private static void showRoleDashboard() {
        switch (currentUser.getRole()) {
            case "ADMIN":   adminMenu();   break;
            case "MANAGER": managerMenu(); break;
            case "AGENT":   agentMenu();   break;
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
            case 1:  leadsMenu(true);      break;
            case 2:  customersMenu(true);  break;
            case 3:  productsMenu(true);   break;
            case 4:  promotionsMenu(true); break;
            case 5:  createOrderMenu();    break;
            case 6:  viewOrdersMenu();     break;
            case 7:  manageUsers();        break;
            case 8:  reportsMenu();        break;
            case 9:  viewSystemLogs();     break;
            case 10: logout();            break;
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
            case 1:  leadsMenu(true);      break;
            case 2:  customersMenu(true);  break;
            case 3:  productsMenu(false);  break;
            case 4:  promotionsMenu(true); break;
            case 5:  createOrderMenu();    break;
            case 6:  viewOrdersMenu();     break;
            case 7:  reportsMenu();        break;
            case 8:  logout();            break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  AGENT MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void agentMenu() {
        ConsoleUtil.printHeader("AGENT DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  addLead();                                  break;
            case 2:  viewMyLeads();                              break;
            case 3:  updateLeadStatus();                         break;
            case 4:  logLeadActivity();                          break;
            case 5:  convertLeadToCustomer();                    break;
            case 6:  customersMenu(false);                       break;
            case 7:  createOrderMenu();                          break;
            case 8:  viewProductsAndPromotions();                break;
            case 9:  logout();                                  break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LEAD MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void leadsMenu(boolean fullAccess) {
        ConsoleUtil.printSection("LEAD MANAGEMENT");
        System.out.println("  1. Add New Lead");
        System.out.println("  2. View All Leads");
        System.out.println("  3. View Leads by Status");
        System.out.println("  4. Update Lead Status");
        System.out.println("  5. Log Activity on Lead");
        System.out.println("  6. View Lead Activities");
        System.out.println("  7. Convert Lead to Customer");
        if (fullAccess) System.out.println("  8. Reassign Lead");
        System.out.println("  " + (fullAccess ? "9" : "8") + ". Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1: addLead();             break;
            case 2: viewAllLeads();        break;
            case 3: viewLeadsByStatus();   break;
            case 4: updateLeadStatus();    break;
            case 5: logLeadActivity();     break;
            case 6: viewLeadActivities();  break;
            case 7: convertLeadToCustomer();break;
            case 8: if (fullAccess) reassignLead(); break;
        }
    }

    private static void addLead() {
        ConsoleUtil.printSection("ADD NEW LEAD");
        Lead l = new Lead();
        System.out.print("  Lead Code (e.g. LED-010)   : "); l.setLeadCode(sc.nextLine().trim());
        System.out.print("  First Name                 : "); l.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name                  : "); l.setLastName(sc.nextLine().trim());
        System.out.print("  Email                      : "); l.setEmail(sc.nextLine().trim());
        System.out.print("  Phone                      : "); l.setPhone(sc.nextLine().trim());
        System.out.print("  Company                    : "); l.setCompany(sc.nextLine().trim());
        System.out.print("  Designation                : "); l.setDesignation(sc.nextLine().trim());
        System.out.print("  City                       : "); l.setCity(sc.nextLine().trim());
        System.out.print("  State                      : "); l.setState(sc.nextLine().trim());
        System.out.println("  Source: WEBSITE/REFERRAL/SOCIAL_MEDIA/EMAIL_CAMPAIGN/COLD_CALL/EVENT/OTHER");
        System.out.print("  Source                     : "); l.setSource(sc.nextLine().trim().toUpperCase());
        System.out.print("  Interest Area              : "); l.setInterestArea(sc.nextLine().trim());
        System.out.print("  Budget Range               : "); l.setBudgetRange(sc.nextLine().trim());
        System.out.println("  Priority: LOW / MEDIUM / HIGH");
        System.out.print("  Priority                   : "); l.setPriority(sc.nextLine().trim().toUpperCase());
        System.out.print("  Notes                      : "); l.setNotes(sc.nextLine().trim());
        l.setAssignedTo(currentUser.getUserId());

        if (leadDAO.addLead(l, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Lead added! ID: " + l.getLeadId());
            userDAO.logAction(currentUser.getUserId(), "ADD_LEAD", "Code=" + l.getLeadCode());
        } else ConsoleUtil.printError("Failed. Lead code may already exist.");
    }

    private static void viewAllLeads() {
        ConsoleUtil.printSection("ALL LEADS");
        List<Lead> list = leadDAO.getAllLeads();
        if (list.isEmpty()) { System.out.println("  No leads found."); return; }
        printLeadTable(list);
    }

    private static void viewMyLeads() {
        ConsoleUtil.printSection("MY LEADS");
        List<Lead> list = leadDAO.getLeadsByUser(currentUser.getUserId());
        if (list.isEmpty()) { System.out.println("  No leads assigned to you."); return; }
        printLeadTable(list);
    }

    private static void viewLeadsByStatus() {
        System.out.println("  Status: NEW / CONTACTED / QUALIFIED / PROPOSAL_SENT / NEGOTIATION / CONVERTED / LOST");
        System.out.print("  Filter by status: "); String status = sc.nextLine().trim().toUpperCase();
        ConsoleUtil.printSection("LEADS — " + status);
        List<Lead> list = leadDAO.getLeadsByStatus(status);
        if (list.isEmpty()) { System.out.println("  No leads found with status: " + status); return; }
        printLeadTable(list);
    }

    private static void printLeadTable(List<Lead> list) {
        System.out.printf("  %-5s %-10s %-22s %-20s %-18s %-16s %-8s %-15s%n",
            "ID","Code","Name","Company","Interest","Status","Priority","Assigned");
        System.out.println("  " + "-".repeat(117));
        for (Lead l : list)
            System.out.printf("  %-5d %-10s %-22s %-20s %-18s %-16s %-8s %-15s%n",
                l.getLeadId(), l.getLeadCode(), l.getFullName(), nvl(l.getCompany()),
                nvl(l.getInterestArea()), l.getStatus(), l.getPriority(), nvl(l.getAssignedToName()));
    }

    private static void updateLeadStatus() {
        viewAllLeads();
        System.out.print("\n  Lead ID to update: "); int id = readInt();
        System.out.println("  Status: NEW/CONTACTED/QUALIFIED/PROPOSAL_SENT/NEGOTIATION/CONVERTED/LOST");
        System.out.print("  New Status : "); String status = sc.nextLine().trim().toUpperCase();
        System.out.print("  Notes/Remarks: "); String notes = sc.nextLine().trim();
        if (leadDAO.updateLeadStatus(id, status, notes))
            ConsoleUtil.printSuccess("Lead status updated to " + status);
        else ConsoleUtil.printError("Failed.");
    }

    private static void logLeadActivity() {
        viewAllLeads();
        System.out.print("\n  Lead ID: "); int leadId = readInt();
        LeadActivity a = new LeadActivity();
        a.setLeadId(leadId);
        System.out.println("  Type: CALL/EMAIL/MEETING/DEMO/PROPOSAL/FOLLOW_UP/NOTE");
        System.out.print("  Activity Type   : "); a.setActivityType(sc.nextLine().trim().toUpperCase());
        System.out.print("  Description     : "); a.setDescription(sc.nextLine().trim());
        System.out.print("  Outcome         : "); a.setOutcome(sc.nextLine().trim());
        System.out.print("  Next Action     : "); a.setNextAction(sc.nextLine().trim());
        System.out.print("  Next Action Date (YYYY-MM-DD): "); a.setNextActionDate(sc.nextLine().trim());
        a.setPerformedByName(String.valueOf(currentUser.getUserId()));

        if (leadDAO.logActivity(a)) {
            ConsoleUtil.printSuccess("Activity logged successfully.");
            userDAO.logAction(currentUser.getUserId(), "LOG_ACTIVITY", "LeadID=" + leadId + " Type=" + a.getActivityType());
        } else ConsoleUtil.printError("Failed.");
    }

    private static void viewLeadActivities() {
        viewAllLeads();
        System.out.print("\n  Lead ID to view activities: "); int id = readInt();
        Lead lead = leadDAO.getLeadById(id);
        if (lead == null) { ConsoleUtil.printError("Lead not found."); return; }
        ConsoleUtil.printSection("ACTIVITIES — " + lead.getFullName());
        List<LeadActivity> acts = leadDAO.getActivitiesByLead(id);
        if (acts.isEmpty()) { System.out.println("  No activities recorded."); return; }
        for (LeadActivity a : acts) {
            System.out.println("  " + "-".repeat(60));
            System.out.printf("  [%d] %s | %s | By: %s%n", a.getActivityId(), a.getActivityType(), a.getActivityDate(), nvl(a.getPerformedByName()));
            System.out.printf("  Description  : %s%n", nvl(a.getDescription()));
            System.out.printf("  Outcome      : %s%n", nvl(a.getOutcome()));
            System.out.printf("  Next Action  : %s (by %s)%n", nvl(a.getNextAction()), nvl(a.getNextActionDate()));
        }
    }

    private static void convertLeadToCustomer() {
        ConsoleUtil.printSection("CONVERT LEAD TO CUSTOMER");
        List<Lead> qualified = leadDAO.getLeadsByStatus("QUALIFIED");
        qualified.addAll(leadDAO.getLeadsByStatus("NEGOTIATION"));
        qualified.addAll(leadDAO.getLeadsByStatus("PROPOSAL_SENT"));
        if (qualified.isEmpty()) { System.out.println("  No qualified/negotiation leads to convert."); return; }
        printLeadTable(qualified);
        System.out.print("\n  Lead ID to convert: "); int leadId = readInt();
        Lead lead = leadDAO.getLeadById(leadId);
        if (lead == null) { ConsoleUtil.printError("Lead not found."); return; }

        Customer c = new Customer();
        System.out.println("  Pre-filled from lead. Confirm or update:");
        System.out.print("  Customer Code (e.g. CUS-005): "); c.setCustomerCode(sc.nextLine().trim());
        c.setFirstName(lead.getFirstName()); c.setLastName(lead.getLastName());
        c.setEmail(lead.getEmail()); c.setPhone(lead.getPhone());
        c.setCompany(lead.getCompany()); c.setDesignation(lead.getDesignation());
        c.setCity(lead.getCity()); c.setState(lead.getState());
        System.out.print("  Address         : "); c.setAddress(sc.nextLine().trim());
        System.out.print("  Pincode         : "); c.setPincode(sc.nextLine().trim());
        System.out.println("  Type: INDIVIDUAL / CORPORATE / SME / ENTERPRISE");
        System.out.print("  Customer Type   : "); c.setCustomerType(sc.nextLine().trim().toUpperCase());
        System.out.println("  Segment: PREMIUM / STANDARD / BASIC");
        System.out.print("  Segment         : "); c.setSegment(sc.nextLine().trim().toUpperCase());

        if (customerDAO.convertLeadToCustomer(leadId, c, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Lead converted! Customer ID: " + c.getCustomerId());
            userDAO.logAction(currentUser.getUserId(), "CONVERT_LEAD", "LeadID=" + leadId + " CusCode=" + c.getCustomerCode());
        } else ConsoleUtil.printError("Failed. Customer code may exist.");
    }

    private static void reassignLead() {
        viewAllLeads();
        System.out.print("\n  Lead ID to reassign: "); int leadId = readInt();
        System.out.println("  Available Agents:");
        userDAO.getAllUsers().stream()
            .filter(u -> "AGENT".equals(u.getRole()) && u.isStatus())
            .forEach(u -> System.out.printf("    [%d] %s%n", u.getUserId(), u.getUsername()));
        System.out.print("  Assign to User ID: "); int userId = readInt();
        if (leadDAO.reassignLead(leadId, userId)) ConsoleUtil.printSuccess("Lead reassigned.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOMER MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void customersMenu(boolean fullAccess) {
        ConsoleUtil.printSection("CUSTOMER MANAGEMENT");
        System.out.println("  1. Add New Customer");
        System.out.println("  2. View All Customers");
        System.out.println("  3. View Customer Profile");
        System.out.println("  4. View Customer Orders");
        if (fullAccess) {
            System.out.println("  5. Update Customer Status");
            System.out.println("  6. Update Segment");
            System.out.println("  7. Filter by Segment");
            System.out.println("  8. Back");
        } else {
            System.out.println("  5. Back");
        }
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1: addCustomer();           break;
            case 2: viewAllCustomers();      break;
            case 3: viewCustomerProfile();   break;
            case 4: viewCustomerOrders();    break;
            case 5: if(fullAccess) updateCustomerStatus(); break;
            case 6: if(fullAccess) updateSegment();        break;
            case 7: if(fullAccess) filterBySegment();      break;
        }
    }

    private static void addCustomer() {
        ConsoleUtil.printSection("ADD NEW CUSTOMER");
        Customer c = new Customer();
        System.out.print("  Customer Code (e.g. CUS-006) : "); c.setCustomerCode(sc.nextLine().trim());
        System.out.print("  First Name                   : "); c.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name                    : "); c.setLastName(sc.nextLine().trim());
        System.out.print("  Email                        : "); c.setEmail(sc.nextLine().trim());
        System.out.print("  Phone                        : "); c.setPhone(sc.nextLine().trim());
        System.out.print("  Company                      : "); c.setCompany(sc.nextLine().trim());
        System.out.print("  Designation                  : "); c.setDesignation(sc.nextLine().trim());
        System.out.print("  Address                      : "); c.setAddress(sc.nextLine().trim());
        System.out.print("  City                         : "); c.setCity(sc.nextLine().trim());
        System.out.print("  State                        : "); c.setState(sc.nextLine().trim());
        System.out.print("  Pincode                      : "); c.setPincode(sc.nextLine().trim());
        System.out.println("  Type: INDIVIDUAL/CORPORATE/SME/ENTERPRISE");
        System.out.print("  Customer Type                : "); c.setCustomerType(sc.nextLine().trim().toUpperCase());
        System.out.println("  Segment: PREMIUM/STANDARD/BASIC");
        System.out.print("  Segment                      : "); c.setSegment(sc.nextLine().trim().toUpperCase());

        if (customerDAO.addCustomer(c, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Customer added! ID: " + c.getCustomerId());
            userDAO.logAction(currentUser.getUserId(), "ADD_CUSTOMER", "Code=" + c.getCustomerCode());
        } else ConsoleUtil.printError("Failed. Customer code may already exist.");
    }

    private static void viewAllCustomers() {
        ConsoleUtil.printSection("ALL CUSTOMERS");
        List<Customer> list = customerDAO.getAllCustomers();
        if (list.isEmpty()) { System.out.println("  No customers found."); return; }
        System.out.printf("  %-5s %-10s %-22s %-20s %-12s %-10s %-14s %-10s%n",
            "ID","Code","Name","Company","Type","Segment","Purchases","Status");
        System.out.println("  " + "-".repeat(103));
        for (Customer c : list)
            System.out.printf("  %-5d %-10s %-22s %-20s %-12s %-10s %-14.2f %-10s%n",
                c.getCustomerId(), c.getCustomerCode(), c.getFullName(),
                nvl(c.getCompany()), c.getCustomerType(), c.getSegment(),
                c.getTotalPurchases(), c.getStatus());
    }

    private static void viewCustomerProfile() {
        viewAllCustomers();
        System.out.print("\n  Customer ID: "); int id = readInt();
        Customer c = customerDAO.getCustomerById(id);
        if (c == null) { ConsoleUtil.printError("Not found."); return; }
        ConsoleUtil.printSection("CUSTOMER PROFILE");
        System.out.printf("  %-22s: %s%n","Code",c.getCustomerCode());
        System.out.printf("  %-22s: %s%n","Name",c.getFullName());
        System.out.printf("  %-22s: %s%n","Company",nvl(c.getCompany()));
        System.out.printf("  %-22s: %s%n","Designation",nvl(c.getDesignation()));
        System.out.printf("  %-22s: %s%n","Email",nvl(c.getEmail()));
        System.out.printf("  %-22s: %s%n","Phone",nvl(c.getPhone()));
        System.out.printf("  %-22s: %s, %s%n","City/State",nvl(c.getCity()),nvl(c.getState()));
        System.out.printf("  %-22s: %s%n","Type",c.getCustomerType());
        System.out.printf("  %-22s: %s%n","Segment",c.getSegment());
        System.out.printf("  %-22s: Rs. %.2f%n","Total Purchases",c.getTotalPurchases());
        System.out.printf("  %-22s: %d pts%n","Loyalty Points",c.getLoyaltyPoints());
        System.out.printf("  %-22s: %s%n","Status",c.getStatus());
    }

    private static void viewCustomerOrders() {
        viewAllCustomers();
        System.out.print("\n  Customer ID: "); int id = readInt();
        ConsoleUtil.printSection("ORDERS FOR CUSTOMER " + id);
        List<Order> orders = orderDAO.getOrdersByCustomer(id);
        if (orders.isEmpty()) { System.out.println("  No orders found."); return; }
        for (Order o : orders) {
            System.out.printf("  [%s] %s | Total: Rs.%.2f | Discount: Rs.%.2f | Final: Rs.%.2f | Status: %s%n",
                o.getOrderCode(), o.getOrderDate(), o.getTotalAmount(), o.getDiscountAmount(), o.getFinalAmount(), o.getStatus());
            orderDAO.printOrderItems(o.getOrderId());
            System.out.println();
        }
    }

    private static void updateCustomerStatus() {
        viewAllCustomers();
        System.out.print("\n  Customer ID: "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / BLOCKED");
        System.out.print("  New Status: "); String s = sc.nextLine().trim().toUpperCase();
        if (customerDAO.updateCustomerStatus(id, s)) ConsoleUtil.printSuccess("Updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateSegment() {
        viewAllCustomers();
        System.out.print("\n  Customer ID: "); int id = readInt();
        System.out.println("  Segment: PREMIUM / STANDARD / BASIC");
        System.out.print("  New Segment: "); String s = sc.nextLine().trim().toUpperCase();
        if (customerDAO.updateSegment(id, s)) ConsoleUtil.printSuccess("Segment updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void filterBySegment() {
        System.out.println("  Segment: PREMIUM / STANDARD / BASIC");
        System.out.print("  Filter: "); String seg = sc.nextLine().trim().toUpperCase();
        ConsoleUtil.printSection("CUSTOMERS — " + seg);
        List<Customer> list = customerDAO.getCustomersBySegment(seg);
        if (list.isEmpty()) { System.out.println("  No customers in this segment."); return; }
        for (Customer c : list)
            System.out.printf("  [%d] %s | %s | Rs.%.2f purchases | %d pts%n",
                c.getCustomerId(), c.getFullName(), nvl(c.getCompany()), c.getTotalPurchases(), c.getLoyaltyPoints());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRODUCT MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void productsMenu(boolean fullEdit) {
        ConsoleUtil.printSection("PRODUCT MANAGEMENT");
        System.out.println("  1. View All Products");
        if (fullEdit) {
            System.out.println("  2. Add New Product");
            System.out.println("  3. Update Product Price");
            System.out.println("  4. Update Stock Quantity");
            System.out.println("  5. Update Product Status");
            System.out.println("  6. Back");
        } else {
            System.out.println("  2. Back");
        }
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1: viewAllProducts(fullEdit);   break;
            case 2: if(fullEdit) addProduct();   break;
            case 3: if(fullEdit) updateProductPrice();  break;
            case 4: if(fullEdit) updateStock();         break;
            case 5: if(fullEdit) updateProductStatus(); break;
        }
    }

    private static void viewAllProducts(boolean showAll) {
        ConsoleUtil.printSection("PRODUCTS");
        List<Product> list = showAll ? productDAO.getAllProductsAdmin() : productDAO.getAllProducts();
        if (list.isEmpty()) { System.out.println("  No products found."); return; }
        System.out.printf("  %-5s %-10s %-30s %-16s %-12s %-8s %-12s%n","ID","Code","Name","Category","Price","Stock","Status");
        System.out.println("  " + "-".repeat(95));
        for (Product p : list)
            System.out.printf("  %-5d %-10s %-30s %-16s %-12.2f %-8d %-12s%n",
                p.getProductId(), p.getProductCode(), p.getProductName(),
                nvl(p.getCategory()), p.getUnitPrice(), p.getStockQuantity(), p.getStatus());
    }

    private static void addProduct() {
        ConsoleUtil.printSection("ADD NEW PRODUCT");
        Product p = new Product();
        System.out.print("  Product Code (e.g. PRD-007)  : "); p.setProductCode(sc.nextLine().trim());
        System.out.print("  Product Name                 : "); p.setProductName(sc.nextLine().trim());
        System.out.print("  Category                     : "); p.setCategory(sc.nextLine().trim());
        System.out.print("  Description                  : "); p.setDescription(sc.nextLine().trim());
        System.out.print("  Unit Price (Rs.)             : "); p.setUnitPrice(readDouble());
        System.out.print("  Cost Price (Rs.)             : "); p.setCostPrice(readDouble());
        System.out.print("  Stock Quantity               : "); p.setStockQuantity(readInt());
        System.out.print("  Unit (e.g. License/Package)  : "); p.setUnit(sc.nextLine().trim());
        System.out.print("  Brand                        : "); p.setBrand(sc.nextLine().trim());
        if (productDAO.addProduct(p, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Product added! ID: " + p.getProductId());
            userDAO.logAction(currentUser.getUserId(), "ADD_PRODUCT", "Code=" + p.getProductCode());
        } else ConsoleUtil.printError("Failed. Product code may exist.");
    }

    private static void updateProductPrice() {
        viewAllProducts(true);
        System.out.print("\n  Product ID: "); int id = readInt();
        System.out.print("  New Price (Rs.): "); double price = readDouble();
        if (productDAO.updatePrice(id, price)) ConsoleUtil.printSuccess("Price updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateStock() {
        viewAllProducts(true);
        System.out.print("\n  Product ID: "); int id = readInt();
        System.out.print("  Quantity to add (+) or deduct (-): "); int qty = readInt();
        if (productDAO.updateStock(id, qty)) ConsoleUtil.printSuccess("Stock updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateProductStatus() {
        viewAllProducts(true);
        System.out.print("\n  Product ID: "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / DISCONTINUED");
        System.out.print("  New Status: "); String s = sc.nextLine().trim().toUpperCase();
        if (productDAO.updateProductStatus(id, s)) ConsoleUtil.printSuccess("Status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PROMOTIONS MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void promotionsMenu(boolean fullEdit) {
        ConsoleUtil.printSection("PROMOTIONS MANAGEMENT");
        System.out.println("  1. View All Promotions");
        System.out.println("  2. View Active Promotions");
        if (fullEdit) {
            System.out.println("  3. Add New Promotion");
            System.out.println("  4. Link Product to Promotion");
            System.out.println("  5. Update Promotion Status");
            System.out.println("  6. Back");
        } else {
            System.out.println("  3. Back");
        }
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1: viewAllPromotions();              break;
            case 2: viewActivePromotions();           break;
            case 3: if(fullEdit) addPromotion();      break;
            case 4: if(fullEdit) linkProductToPromo();break;
            case 5: if(fullEdit) updatePromoStatus(); break;
        }
    }

    private static void viewAllPromotions() {
        ConsoleUtil.printSection("ALL PROMOTIONS");
        List<Promotion> list = promoDAO.getAllPromotions();
        printPromoTable(list);
    }

    private static void viewActivePromotions() {
        ConsoleUtil.printSection("ACTIVE PROMOTIONS");
        List<Promotion> list = promoDAO.getActivePromotions();
        if (list.isEmpty()) { System.out.println("  No active promotions currently."); return; }
        printPromoTable(list);
    }

    private static void printPromoTable(List<Promotion> list) {
        if (list.isEmpty()) { System.out.println("  No promotions found."); return; }
        System.out.printf("  %-5s %-12s %-25s %-20s %-8s %-12s %-12s %-8s%n",
            "ID","Code","Name","Type","Value","Min.Purchase","End Date","Status");
        System.out.println("  " + "-".repeat(105));
        for (Promotion p : list)
            System.out.printf("  %-5d %-12s %-25s %-20s %-8.1f %-12.2f %-12s %-8s%n",
                p.getPromoId(), p.getPromoCode(), p.getPromoName(), p.getPromoType(),
                p.getDiscountValue(), p.getMinPurchase(), p.getEndDate(), p.getStatus());
    }

    private static void addPromotion() {
        ConsoleUtil.printSection("ADD NEW PROMOTION");
        Promotion p = new Promotion();
        System.out.print("  Promo Code (e.g. PROMO-NEW)  : "); p.setPromoCode(sc.nextLine().trim());
        System.out.print("  Promo Name                   : "); p.setPromoName(sc.nextLine().trim());
        System.out.println("  Type: PERCENTAGE_DISCOUNT / FLAT_DISCOUNT / BUY_X_GET_Y / BUNDLE");
        System.out.print("  Type                         : "); p.setPromoType(sc.nextLine().trim().toUpperCase());
        System.out.print("  Discount Value (% or Rs.)    : "); p.setDiscountValue(readDouble());
        System.out.print("  Minimum Purchase (Rs.)       : "); p.setMinPurchase(readDouble());
        System.out.print("  Maximum Discount (Rs., 0=none): "); p.setMaxDiscount(readDouble());
        System.out.print("  Start Date (YYYY-MM-DD)      : "); p.setStartDate(sc.nextLine().trim());
        System.out.print("  End Date   (YYYY-MM-DD)      : "); p.setEndDate(sc.nextLine().trim());
        System.out.print("  Usage Limit                  : "); p.setUsageLimit(readInt());
        System.out.println("  Applicable To: ALL / PREMIUM / CORPORATE / SME");
        System.out.print("  Applicable To                : "); p.setApplicableTo(sc.nextLine().trim().toUpperCase());
        System.out.print("  Description                  : "); p.setDescription(sc.nextLine().trim());
        if (promoDAO.addPromotion(p, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Promotion added! ID: " + p.getPromoId());
            userDAO.logAction(currentUser.getUserId(), "ADD_PROMOTION", "Code=" + p.getPromoCode());
        } else ConsoleUtil.printError("Failed. Promo code may already exist.");
    }

    private static void linkProductToPromo() {
        viewAllProducts(false);
        System.out.print("\n  Product ID: "); int pid = readInt();
        viewAllPromotions();
        System.out.print("\n  Promo ID  : "); int proid = readInt();
        if (promoDAO.linkProductToPromo(pid, proid)) ConsoleUtil.printSuccess("Product linked to promotion.");
        else ConsoleUtil.printError("Failed or already linked.");
    }

    private static void updatePromoStatus() {
        viewAllPromotions();
        System.out.print("\n  Promo ID: "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / EXPIRED");
        System.out.print("  New Status: "); String s = sc.nextLine().trim().toUpperCase();
        if (promoDAO.updatePromoStatus(id, s)) ConsoleUtil.printSuccess("Status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ORDER MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void createOrderMenu() {
        ConsoleUtil.printSection("CREATE NEW ORDER");
        viewAllCustomers();
        System.out.print("\n  Customer ID : "); int customerId = readInt();
        Customer cust = customerDAO.getCustomerById(customerId);
        if (cust == null) { ConsoleUtil.printError("Customer not found."); return; }
        System.out.println("  Customer: " + cust.getFullName() + " [" + cust.getSegment() + "]");

        // Build items list
        List<int[]> items = new ArrayList<>();
        viewAllProducts(false);
        System.out.println("\n  Add products (enter 0 to finish):");
        while (true) {
            System.out.print("  Product ID (0=done): "); int pid = readInt();
            if (pid == 0) break;
            Product prod = productDAO.getProductById(pid);
            if (prod == null) { ConsoleUtil.printError("Product not found. Try again."); continue; }
            System.out.print("  Quantity for " + prod.getProductName() + ": "); int qty = readInt();
            items.add(new int[]{pid, qty});
            System.out.printf("  Added: %s x%d @ Rs.%.2f = Rs.%.2f%n",
                prod.getProductName(), qty, prod.getUnitPrice(), prod.getUnitPrice() * qty);
        }
        if (items.isEmpty()) { ConsoleUtil.printError("No items added. Order cancelled."); return; }

        // Optional promotion
        System.out.println("\n  Active Promotions:");
        viewActivePromotions();
        System.out.print("  Apply Promo ID (0=none): "); int promoId = readInt();
        System.out.print("  Order Notes : "); String notes = sc.nextLine().trim();

        int[][] itemArr = items.toArray(new int[0][]);
        if (orderDAO.createOrder(customerId, itemArr, promoId, notes,
                currentUser.getUserId(), productDAO, promoDAO, customerDAO)) {
            ConsoleUtil.printSuccess("Order created successfully!");
            userDAO.logAction(currentUser.getUserId(), "CREATE_ORDER", "CustomerID=" + customerId + " Items=" + items.size());
        } else ConsoleUtil.printError("Failed to create order.");
    }

    private static void viewOrdersMenu() {
        ConsoleUtil.printSection("ALL ORDERS");
        List<Order> list = orderDAO.getAllOrders();
        if (list.isEmpty()) { System.out.println("  No orders found."); return; }
        System.out.printf("  %-5s %-15s %-22s %-12s %-12s %-12s %-12s %-12s%n",
            "ID","Order Code","Customer","Total","Discount","Final","Promo","Status");
        System.out.println("  " + "-".repeat(106));
        for (Order o : list)
            System.out.printf("  %-5d %-15s %-22s %-12.2f %-12.2f %-12.2f %-12s %-12s%n",
                o.getOrderId(), o.getOrderCode(), o.getCustomerName(),
                o.getTotalAmount(), o.getDiscountAmount(), o.getFinalAmount(),
                nvl(o.getPromoCode()), o.getStatus());

        System.out.print("\n  View items for Order ID (0=back): "); int id = readInt();
        if (id > 0) {
            ConsoleUtil.printSection("ORDER ITEMS");
            orderDAO.printOrderItems(id);
        }
    }

    private static void viewProductsAndPromotions() {
        viewAllProducts(false);
        System.out.println();
        viewActivePromotions();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void reportsMenu() {
        ConsoleUtil.printSection("REPORTS");
        System.out.println("  1. Lead Pipeline Summary");
        System.out.println("  2. Customer Summary");
        System.out.println("  3. Product Catalogue");
        System.out.println("  4. Revenue Report (by month)");
        System.out.println("  5. Active Promotions");
        System.out.println("  6. Conversion Rate");
        System.out.println("  7. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: leadPipelineReport();  break;
            case 2: viewAllCustomers();    break;
            case 3: viewAllProducts(false);break;
            case 4: orderDAO.printRevenueReport(); break;
            case 5: viewActivePromotions();break;
            case 6: conversionRateReport();break;
        }
    }

    private static void leadPipelineReport() {
        ConsoleUtil.printSection("LEAD PIPELINE");
        String[] statuses = {"NEW","CONTACTED","QUALIFIED","PROPOSAL_SENT","NEGOTIATION","CONVERTED","LOST"};
        System.out.printf("  %-20s %-8s%n","Status","Count");
        System.out.println("  " + "-".repeat(30));
        int total = 0;
        for (String s : statuses) {
            int count = leadDAO.getLeadsByStatus(s).size();
            System.out.printf("  %-20s %-8d%n", s, count);
            total += count;
        }
        System.out.println("  " + "-".repeat(30));
        System.out.printf("  %-20s %-8d%n","TOTAL",total);
    }

    private static void conversionRateReport() {
        ConsoleUtil.printSection("CONVERSION RATE REPORT");
        List<Lead> all       = leadDAO.getAllLeads();
        List<Lead> converted = leadDAO.getLeadsByStatus("CONVERTED");
        List<Lead> lost      = leadDAO.getLeadsByStatus("LOST");
        int total = all.size();
        System.out.printf("  Total Leads      : %d%n", total);
        System.out.printf("  Converted        : %d%n", converted.size());
        System.out.printf("  Lost             : %d%n", lost.size());
        if (total > 0) {
            System.out.printf("  Conversion Rate  : %.1f%%%n", (converted.size() * 100.0 / total));
            System.out.printf("  Loss Rate        : %.1f%%%n", (lost.size() * 100.0 / total));
        }
        System.out.printf("  Total Customers  : %d%n", customerDAO.getAllCustomers().size());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN EXTRAS
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageUsers() {
        ConsoleUtil.printSection("MANAGE USERS");
        List<User> list = userDAO.getAllUsers();
        System.out.printf("  %-5s %-20s %-30s %-12s %-8s%n","ID","Username","Email","Role","Active");
        System.out.println("  " + "-".repeat(77));
        for (User u : list)
            System.out.printf("  %-5d %-20s %-30s %-12s %-8s%n",
                u.getUserId(), u.getUsername(), u.getEmail(), u.getRole(), u.isStatus()?"Yes":"No");
        System.out.println("\n  1. Add User  2. Deactivate User  3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            System.out.print("  Username : "); String un = sc.nextLine().trim();
            System.out.print("  Password : "); String pw = sc.nextLine().trim();
            System.out.print("  Email    : "); String em = sc.nextLine().trim();
            System.out.println("  Role: ADMIN / MANAGER / AGENT");
            System.out.print("  Role     : "); String rl = sc.nextLine().trim().toUpperCase();
            if (userDAO.createUser(un,pw,em,rl)) ConsoleUtil.printSuccess("User created.");
            else ConsoleUtil.printError("Failed.");
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
        userDAO.logAction(currentUser.getUserId(),"LOGOUT","User logged out");
        ConsoleUtil.printSuccess("Logged out. Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }
    private static int    readInt()    { try { return Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { return -1; } }
    private static double readDouble() { try { return Double.parseDouble(sc.nextLine().trim()); } catch (Exception e) { return 0.0; } }
    private static String nvl(String s){ return s == null ? "-" : s; }
}
