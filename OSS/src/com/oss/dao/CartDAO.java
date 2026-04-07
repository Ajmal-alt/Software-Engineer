package com.oss.dao;

import com.oss.model.CartItem;
import com.oss.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private Connection conn;

    public CartDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addToCart(int customerId, int productId, int quantity) {
        // If already in cart, update quantity
        String check = "SELECT cart_id, quantity FROM cart WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int newQty = rs.getInt("quantity") + quantity;
                try (PreparedStatement upd = conn.prepareStatement(
                        "UPDATE cart SET quantity = ? WHERE cart_id = ?")) {
                    upd.setInt(1, newQty);
                    upd.setInt(2, rs.getInt("cart_id"));
                    return upd.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, ?)")) {
                    ins.setInt(1, customerId);
                    ins.setInt(2, productId);
                    ins.setInt(3, quantity);
                    return ins.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addToCart: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCartQty(int cartId, int quantity) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE cart SET quantity = ? WHERE cart_id = ?")) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] updateCartQty: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFromCart(int cartId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart WHERE cart_id = ?")) {
            ps.setInt(1, cartId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] removeFromCart: " + e.getMessage());
            return false;
        }
    }

    public boolean clearCart(int customerId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart WHERE customer_id = ?")) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] clearCart: " + e.getMessage());
            return false;
        }
    }

    public List<CartItem> getCartByCustomer(int customerId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT c.cart_id, c.customer_id, c.product_id, c.quantity, c.added_at, "
                   + "p.product_name, p.product_code, p.unit_price, p.discount_pct, p.tax_percent, p.stock_qty "
                   + "FROM cart c JOIN products p ON c.product_id = p.product_id "
                   + "WHERE c.customer_id = ? ORDER BY c.added_at";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartId(rs.getInt("cart_id"));
                item.setCustomerId(rs.getInt("customer_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setProductCode(rs.getString("product_code"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setDiscountPct(rs.getDouble("discount_pct"));
                item.setTaxPercent(rs.getDouble("tax_percent"));
                item.setStockQty(rs.getInt("stock_qty"));
                item.setQuantity(rs.getInt("quantity"));
                item.setAddedAt(rs.getString("added_at"));
                // line total = qty * unit_price * (1 - discount/100)
                double effective = rs.getDouble("unit_price") * (1 - rs.getDouble("discount_pct") / 100.0);
                item.setLineTotal(effective * rs.getInt("quantity"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getCartByCustomer: " + e.getMessage());
        }
        return list;
    }

    public int getCartCount(int customerId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM cart WHERE customer_id = ?")) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getCartCount: " + e.getMessage());
        }
        return 0;
    }
}
