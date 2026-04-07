package com.oss.dao;

import com.oss.model.Category;
import com.oss.model.Product;
import com.oss.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection conn;

    public ProductDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── Categories ────────────────────────────────────────────────────────────

    public boolean addCategory(Category c) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO categories (category_name, description, status) VALUES (?, ?, 'ACTIVE')",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCategoryName());
            ps.setString(2, c.getDescription());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) c.setCategoryId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addCategory: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCategoryStatus(int categoryId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE categories SET status = ? WHERE category_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(p.product_id) AS product_count "
                   + "FROM categories c LEFT JOIN products p "
                   + "ON c.category_id = p.category_id AND p.status = 'ACTIVE' "
                   + "GROUP BY c.category_id ORDER BY c.category_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                c.setStatus(rs.getString("status"));
                c.setProductCount(rs.getInt("product_count"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllCategories: " + e.getMessage());
        }
        return list;
    }

    // ── Products ──────────────────────────────────────────────────────────────

    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products "
                   + "(product_code, product_name, category_id, description, unit_price, cost_price, "
                   + " stock_qty, unit, brand, tax_percent, discount_pct, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  p.getProductCode());
            ps.setString(2,  p.getProductName());
            ps.setInt(3,     p.getCategoryId());
            ps.setString(4,  p.getDescription());
            ps.setDouble(5,  p.getUnitPrice());
            ps.setDouble(6,  p.getCostPrice());
            ps.setInt(7,     p.getStockQty());
            ps.setString(8,  p.getUnit());
            ps.setString(9,  p.getBrand());
            ps.setDouble(10, p.getTaxPercent());
            ps.setDouble(11, p.getDiscountPct());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) p.setProductId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addProduct: " + e.getMessage());
        }
        return false;
    }

    public boolean updateProductStatus(int productId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET status = ? WHERE product_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateStock(int productId, int qty) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET stock_qty = stock_qty + ? WHERE product_id = ?")) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updatePrice(int productId, double newPrice) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET unit_price = ? WHERE product_id = ?")) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateDiscount(int productId, double discountPct) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET discount_pct = ? WHERE product_id = ?")) {
            ps.setDouble(1, discountPct);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.category_name FROM products p "
                   + "JOIN categories c ON p.category_id = c.category_id WHERE p.product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getProductById: " + e.getMessage());
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p "
                   + "JOIN categories c ON p.category_id = c.category_id ORDER BY p.product_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllProducts: " + e.getMessage());
        }
        return list;
    }

    public List<Product> getActiveProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p "
                   + "JOIN categories c ON p.category_id = c.category_id "
                   + "WHERE p.status = 'ACTIVE' AND p.stock_qty > 0 ORDER BY p.category_id, p.product_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getActiveProducts: " + e.getMessage());
        }
        return list;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p "
                   + "JOIN categories c ON p.category_id = c.category_id "
                   + "WHERE p.status = 'ACTIVE' AND (p.product_name LIKE ? OR p.brand LIKE ? OR c.category_name LIKE ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] searchProducts: " + e.getMessage());
        }
        return list;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p "
                   + "JOIN categories c ON p.category_id = c.category_id "
                   + "WHERE p.category_id = ? AND p.status = 'ACTIVE' ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getProductsByCategory: " + e.getMessage());
        }
        return list;
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductCode(rs.getString("product_code"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setDescription(rs.getString("description"));
        p.setUnitPrice(rs.getDouble("unit_price"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setUnit(rs.getString("unit"));
        p.setBrand(rs.getString("brand"));
        p.setTaxPercent(rs.getDouble("tax_percent"));
        p.setDiscountPct(rs.getDouble("discount_pct"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}
