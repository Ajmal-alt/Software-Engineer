package com.oss.dao;

import com.oss.model.Customer;
import com.oss.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addCustomer(Customer c) {
        try {
            conn.setAutoCommit(false);
            // 1. Create user account
            String uSql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'CUSTOMER')";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(uSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getUsername());
                ps.setString(2, c.getPassword());
                ps.setString(3, c.getEmail());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return false; }
                userId = rs.getInt(1);
                c.setUserId(userId);
            }
            // 2. Create customer profile (12 columns, 12 placeholders)
            String cSql = "INSERT INTO customers "
                        + "(user_id, customer_code, first_name, last_name, phone, "
                        + " date_of_birth, gender, address, city, state, pincode, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
            try (PreparedStatement ps = conn.prepareStatement(cSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1,    userId);
                ps.setString(2, c.getCustomerCode());
                ps.setString(3, c.getFirstName());
                ps.setString(4, c.getLastName());
                ps.setString(5, c.getPhone());
                ps.setString(6, c.getDateOfBirth());
                ps.setString(7, c.getGender());
                ps.setString(8, c.getAddress());
                ps.setString(9, c.getCity());
                ps.setString(10, c.getState());
                ps.setString(11, c.getPincode());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) c.setCustomerId(rs.getInt(1));
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] addCustomer: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCustomerStatus(int customerId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE customers SET status = ? WHERE customer_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateLoyaltyPoints(int customerId, int points) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE customer_id = ?")) {
            ps.setInt(1, points);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT c.*, u.username, u.email AS user_email, u.role "
                   + "FROM customers c JOIN users u ON c.user_id = u.user_id "
                   + "WHERE c.customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getCustomerById: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByUserId(int userId) {
        String sql = "SELECT c.*, u.username, u.email AS user_email, u.role "
                   + "FROM customers c JOIN users u ON c.user_id = u.user_id "
                   + "WHERE c.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getCustomerByUserId: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.email AS user_email, u.role "
                   + "FROM customers c JOIN users u ON c.user_id = u.user_id "
                   + "ORDER BY c.customer_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllCustomers: " + e.getMessage());
        }
        return list;
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCustomerCode(rs.getString("customer_code"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setPhone(rs.getString("phone"));
        c.setDateOfBirth(rs.getString("date_of_birth"));
        c.setGender(rs.getString("gender"));
        c.setAddress(rs.getString("address"));
        c.setCity(rs.getString("city"));
        c.setState(rs.getString("state"));
        c.setPincode(rs.getString("pincode"));
        c.setLoyaltyPoints(rs.getInt("loyalty_points"));
        c.setCustomerStatus(rs.getString("status"));
        c.setUsername(rs.getString("username"));
        c.setEmail(rs.getString("user_email"));
        c.setRole(rs.getString("role"));
        return c;
    }
}
