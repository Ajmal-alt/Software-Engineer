package com.oss.dao;

import com.oss.model.Invoice;
import com.oss.model.Payment;
import com.oss.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private Connection conn;

    public PaymentDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Record payment and auto-generate invoice. Returns payment_id or -1. */
    public int processPayment(int orderId, int customerId, double amount,
                               String mode, String remarks, int processedBy) {
        try {
            conn.setAutoCommit(false);
            String payRef = "PAY-" + System.currentTimeMillis() % 1000000;

            // Insert payment
            int paymentId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payments (payment_ref, order_id, customer_id, amount, "
                    + "payment_mode, status, remarks, processed_by) "
                    + "VALUES (?, ?, ?, ?, ?, 'SUCCESS', ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, payRef);
                ps.setInt(2,    orderId);
                ps.setInt(3,    customerId);
                ps.setDouble(4, amount);
                ps.setString(5, mode);
                ps.setString(6, remarks);
                ps.setInt(7,    processedBy);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return -1; }
                paymentId = rs.getInt(1);
            }

            // Update order status to CONFIRMED
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE orders SET status = 'CONFIRMED' WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            // Auto-generate invoice
            String invNo = "INV-" + System.currentTimeMillis() % 1000000;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO invoices (invoice_no, order_id, payment_id, customer_id, "
                    + "subtotal, tax_amount, discount, shipping, grand_total, status) "
                    + "SELECT ?, o.order_id, ?, o.customer_id, "
                    + "o.subtotal, o.tax_amount, o.discount_amount, o.shipping_charge, o.total_amount, 'ISSUED' "
                    + "FROM orders o WHERE o.order_id = ?")) {
                ps.setString(1, invNo);
                ps.setInt(2,    paymentId);
                ps.setInt(3,    orderId);
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return paymentId;

        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] processPayment: " + e.getMessage());
            return -1;
        }
    }

    public boolean refundPayment(int paymentId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE payments SET status = 'REFUNDED' WHERE payment_id = ?")) {
            ps.setInt(1, paymentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, o.order_code, CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM payments p JOIN orders o ON p.order_id = o.order_id "
                   + "JOIN customers c ON p.customer_id = c.customer_id "
                   + "ORDER BY p.payment_date DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapPayment(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllPayments: " + e.getMessage());
        }
        return list;
    }

    public List<Payment> getPaymentsByCustomer(int customerId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, o.order_code, CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM payments p JOIN orders o ON p.order_id = o.order_id "
                   + "JOIN customers c ON p.customer_id = c.customer_id "
                   + "WHERE p.customer_id = ? ORDER BY p.payment_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapPayment(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getPaymentsByCustomer: " + e.getMessage());
        }
        return list;
    }

    // ── Invoice methods ───────────────────────────────────────────────────────

    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, o.order_code, p.payment_ref, "
                   + "CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM invoices i JOIN orders o ON i.order_id = o.order_id "
                   + "LEFT JOIN payments p ON i.payment_id = p.payment_id "
                   + "JOIN customers c ON i.customer_id = c.customer_id "
                   + "ORDER BY i.invoice_date DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapInvoice(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllInvoices: " + e.getMessage());
        }
        return list;
    }

    public List<Invoice> getInvoicesByCustomer(int customerId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, o.order_code, p.payment_ref, "
                   + "CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM invoices i JOIN orders o ON i.order_id = o.order_id "
                   + "LEFT JOIN payments p ON i.payment_id = p.payment_id "
                   + "JOIN customers c ON i.customer_id = c.customer_id "
                   + "WHERE i.customer_id = ? ORDER BY i.invoice_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapInvoice(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getInvoicesByCustomer: " + e.getMessage());
        }
        return list;
    }

    public void printInvoice(int invoiceId) {
        String sql = "SELECT i.*, o.order_code, o.shipping_address, p.payment_ref, p.payment_mode, "
                   + "CONCAT(c.first_name,' ',c.last_name) AS customer_name, c.phone, c.city, c.state "
                   + "FROM invoices i JOIN orders o ON i.order_id = o.order_id "
                   + "LEFT JOIN payments p ON i.payment_id = p.payment_id "
                   + "JOIN customers c ON i.customer_id = c.customer_id "
                   + "WHERE i.invoice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { System.out.println("  Invoice not found."); return; }
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              ONLINE SHOPPING SYSTEM");
            System.out.println("                    INVOICE");
            System.out.println("=".repeat(60));
            System.out.printf("  Invoice No  : %-25s%n", rs.getString("invoice_no"));
            System.out.printf("  Date        : %-25s%n", rs.getString("invoice_date"));
            System.out.printf("  Order No    : %-25s%n", rs.getString("order_code"));
            System.out.printf("  Payment Ref : %-25s%n", nvl(rs.getString("payment_ref")));
            System.out.printf("  Payment Mode: %-25s%n", nvl(rs.getString("payment_mode")));
            System.out.println("-".repeat(60));
            System.out.printf("  Customer    : %s%n", rs.getString("customer_name"));
            System.out.printf("  Phone       : %s%n", nvl(rs.getString("phone")));
            System.out.printf("  Address     : %s%n", nvl(rs.getString("shipping_address")));
            System.out.println("-".repeat(60));
            System.out.printf("  Subtotal    : Rs. %10.2f%n", rs.getDouble("subtotal"));
            System.out.printf("  Discount    : Rs. %10.2f%n", rs.getDouble("discount"));
            System.out.printf("  Tax         : Rs. %10.2f%n", rs.getDouble("tax_amount"));
            System.out.printf("  Shipping    : Rs. %10.2f%n", rs.getDouble("shipping"));
            System.out.println("-".repeat(60));
            System.out.printf("  GRAND TOTAL : Rs. %10.2f%n", rs.getDouble("grand_total"));
            System.out.println("=".repeat(60));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] printInvoice: " + e.getMessage());
        }
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setPaymentRef(rs.getString("payment_ref"));
        p.setOrderId(rs.getInt("order_id"));
        p.setOrderCode(rs.getString("order_code"));
        p.setCustomerId(rs.getInt("customer_id"));
        p.setCustomerName(rs.getString("customer_name"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMode(rs.getString("payment_mode"));
        p.setPaymentDate(rs.getString("payment_date"));
        p.setStatus(rs.getString("status"));
        p.setRemarks(rs.getString("remarks"));
        return p;
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setInvoiceNo(rs.getString("invoice_no"));
        inv.setOrderId(rs.getInt("order_id"));
        inv.setOrderCode(rs.getString("order_code"));
        inv.setPaymentRef(rs.getString("payment_ref"));
        inv.setCustomerId(rs.getInt("customer_id"));
        inv.setCustomerName(rs.getString("customer_name"));
        inv.setInvoiceDate(rs.getString("invoice_date"));
        inv.setSubtotal(rs.getDouble("subtotal"));
        inv.setTaxAmount(rs.getDouble("tax_amount"));
        inv.setDiscount(rs.getDouble("discount"));
        inv.setShipping(rs.getDouble("shipping"));
        inv.setGrandTotal(rs.getDouble("grand_total"));
        inv.setStatus(rs.getString("status"));
        return inv;
    }

    private String nvl(String s) { return s == null ? "-" : s; }
}
