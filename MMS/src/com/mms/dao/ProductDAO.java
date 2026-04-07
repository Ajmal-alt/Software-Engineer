package com.mms.dao;

import com.mms.model.*;
import com.mms.util.DBConnection;
import java.sql.*;
import java.util.*;

public class ProductDAO {
    private Connection conn;
    public ProductDAO() throws SQLException { this.conn=DBConnection.getInstance().getConnection(); }

    public boolean addProduct(Product p, int createdBy) {
        String sql="INSERT INTO products (product_code,product_name,category,description,unit_price,cost_price,stock_quantity,unit,brand,status,created_by) VALUES (?,?,?,?,?,?,?,?,?,'ACTIVE',?)";
        try (PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,p.getProductCode()); ps.setString(2,p.getProductName());
            ps.setString(3,p.getCategory());    ps.setString(4,p.getDescription());
            ps.setDouble(5,p.getUnitPrice());   ps.setDouble(6,p.getCostPrice());
            ps.setInt(7,p.getStockQuantity());  ps.setString(8,p.getUnit());
            ps.setString(9,p.getBrand());       ps.setInt(10,createdBy);
            if(ps.executeUpdate()>0){ResultSet rs=ps.getGeneratedKeys();if(rs.next())p.setProductId(rs.getInt(1));return true;}
        } catch (SQLException e) { System.out.println("[DB ERROR] addProduct: "+e.getMessage()); }
        return false;
    }

    public boolean updateProductStatus(int productId, String status) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE products SET status=? WHERE product_id=?")) {
            ps.setString(1,status); ps.setInt(2,productId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateStock(int productId, int qty) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE products SET stock_quantity=stock_quantity+? WHERE product_id=?")) {
            ps.setInt(1,qty); ps.setInt(2,productId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public boolean updatePrice(int productId, double newPrice) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE products SET unit_price=? WHERE product_id=?")) {
            ps.setDouble(1,newPrice); ps.setInt(2,productId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public Product getProductById(int productId) {
        try (PreparedStatement ps=conn.prepareStatement("SELECT * FROM products WHERE product_id=?")) {
            ps.setInt(1,productId); ResultSet rs=ps.executeQuery(); if(rs.next()) return map(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getProductById: "+e.getMessage()); }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> list=new ArrayList<>();
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery("SELECT * FROM products WHERE status='ACTIVE' ORDER BY product_id")) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllProducts: "+e.getMessage()); }
        return list;
    }

    public List<Product> getAllProductsAdmin() {
        List<Product> list=new ArrayList<>();
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery("SELECT * FROM products ORDER BY product_id")) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllProductsAdmin: "+e.getMessage()); }
        return list;
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p=new Product();
        p.setProductId(rs.getInt("product_id")); p.setProductCode(rs.getString("product_code"));
        p.setProductName(rs.getString("product_name")); p.setCategory(rs.getString("category"));
        p.setDescription(rs.getString("description")); p.setUnitPrice(rs.getDouble("unit_price"));
        p.setCostPrice(rs.getDouble("cost_price")); p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setUnit(rs.getString("unit")); p.setBrand(rs.getString("brand")); p.setStatus(rs.getString("status"));
        return p;
    }
}
