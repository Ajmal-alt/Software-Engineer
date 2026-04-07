package com.oss.dao;

import com.oss.model.CartItem;
import com.oss.model.Order;
import com.oss.model.OrderItem;
import com.oss.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection conn;

    public OrderDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Place an order from cart items. Returns new order_id or -1 on failure. */
    public int placeOrder(int customerId, List<CartItem> cartItems,
                           String shippingAddress, String notes,
                           int createdBy, CustomerDAO customerDAO) {
        try {
            conn.setAutoCommit(false);

            // Calculate totals
            double subtotal  = 0;
            double taxTotal  = 0;
            double discTotal = 0;
            for (CartItem item : cartItems) {
                double base    = item.getUnitPrice() * item.getQuantity();
                double disc    = base * (item.getDiscountPct() / 100.0);
                double taxable = base - disc;
                double tax     = taxable * (item.getTaxPercent() / 100.0);
                subtotal  += base;
                discTotal += disc;
                taxTotal  += tax;
            }
            double shipping = subtotal > 500 ? 0.00 : 49.00;
            double total    = subtotal - discTotal + taxTotal + shipping;

            // Generate order code
            String orderCode = "ORD-" + System.currentTimeMillis() % 1000000;

            // Insert order
            String oSql = "INSERT INTO orders "
                        + "(order_code, customer_id, subtotal, tax_amount, discount_amount, "
                        + " shipping_charge, total_amount, shipping_address, status, notes, created_by) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, ?)";
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(oSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, orderCode);
                ps.setInt(2,    customerId);
                ps.setDouble(3, subtotal);
                ps.setDouble(4, taxTotal);
                ps.setDouble(5, discTotal);
                ps.setDouble(6, shipping);
                ps.setDouble(7, total);
                ps.setString(8, shippingAddress);
                ps.setString(9, notes);
                ps.setInt(10,   createdBy);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return -1; }
                orderId = rs.getInt(1);
            }

            // Insert order items and deduct stock
            for (CartItem item : cartItems) {
                double base      = item.getUnitPrice() * item.getQuantity();
                double disc      = base * (item.getDiscountPct() / 100.0);
                double taxable   = base - disc;
                double tax       = taxable * (item.getTaxPercent() / 100.0);
                double lineTotal = taxable + tax;

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO order_items "
                        + "(order_id, product_id, product_name, quantity, unit_price, discount_pct, tax_percent, line_total) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1,    orderId);
                    ps.setInt(2,    item.getProductId());
                    ps.setString(3, item.getProductName());
                    ps.setInt(4,    item.getQuantity());
                    ps.setDouble(5, item.getUnitPrice());
                    ps.setDouble(6, item.getDiscountPct());
                    ps.setDouble(7, item.getTaxPercent());
                    ps.setDouble(8, lineTotal);
                    ps.executeUpdate();
                }
                // Deduct stock
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE products SET stock_qty = GREATEST(stock_qty - ?, 0) WHERE product_id = ?")) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.executeUpdate();
                }
            }

            // Award loyalty points (1 point per Rs.100)
            int points = (int)(total / 100);
            customerDAO.updateLoyaltyPoints(customerId, points);

            conn.commit();
            conn.setAutoCommit(true);
            return orderId;

        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] placeOrder: " + e.getMessage());
            return -1;
        }
    }

    public boolean updateOrderStatus(int orderId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status = ? WHERE order_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] updateOrderStatus: " + e.getMessage());
            return false;
        }
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM orders o JOIN customers c ON o.customer_id = c.customer_id "
                   + "WHERE o.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getOrderById: " + e.getMessage());
        }
        return null;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM orders o JOIN customers c ON o.customer_id = c.customer_id "
                   + "ORDER BY o.order_date DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllOrders: " + e.getMessage());
        }
        return list;
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, CONCAT(c.first_name,' ',c.last_name) AS customer_name "
                   + "FROM orders o JOIN customers c ON o.customer_id = c.customer_id "
                   + "WHERE o.customer_id = ? ORDER BY o.order_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getOrdersByCustomer: " + e.getMessage());
        }
        return list;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM order_items WHERE order_id = ? ORDER BY item_id")) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setItemId(rs.getInt("item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setDiscountPct(rs.getDouble("discount_pct"));
                item.setTaxPercent(rs.getDouble("tax_percent"));
                item.setLineTotal(rs.getDouble("line_total"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getOrderItems: " + e.getMessage());
        }
        return list;
    }

    public void printSalesReport() {
        String sql = "SELECT DATE_FORMAT(order_date,'%Y-%m') AS month, COUNT(*) AS orders, "
                   + "SUM(total_amount) AS revenue, SUM(discount_amount) AS discounts, "
                   + "SUM(tax_amount) AS taxes "
                   + "FROM orders WHERE status NOT IN ('CANCELLED','RETURNED') "
                   + "GROUP BY month ORDER BY month DESC LIMIT 12";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("  %-10s %-8s %-14s %-12s %-12s%n",
                    "Month", "Orders", "Revenue", "Discounts", "Tax");
            System.out.println("  " + "-".repeat(58));
            double grandTotal = 0;
            while (rs.next()) {
                System.out.printf("  %-10s %-8d %-14.2f %-12.2f %-12.2f%n",
                        rs.getString("month"), rs.getInt("orders"),
                        rs.getDouble("revenue"), rs.getDouble("discounts"),
                        rs.getDouble("taxes"));
                grandTotal += rs.getDouble("revenue");
            }
            System.out.println("  " + "-".repeat(58));
            System.out.printf("  %-19s Total Revenue: Rs. %.2f%n", "", grandTotal);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] printSalesReport: " + e.getMessage());
        }
    }

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setOrderCode(rs.getString("order_code"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setOrderDate(rs.getString("order_date"));
        o.setSubtotal(rs.getDouble("subtotal"));
        o.setTaxAmount(rs.getDouble("tax_amount"));
        o.setDiscountAmount(rs.getDouble("discount_amount"));
        o.setShippingCharge(rs.getDouble("shipping_charge"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setStatus(rs.getString("status"));
        o.setNotes(rs.getString("notes"));
        return o;
    }
}
