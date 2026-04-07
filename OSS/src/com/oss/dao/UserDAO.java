package com.oss.dao;

import com.oss.model.Customer;
import com.oss.model.User;
import com.oss.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT u.*, c.customer_id, c.customer_code, c.first_name, c.last_name "
                   + "FROM users u LEFT JOIN customers c ON u.user_id = c.user_id "
                   + "WHERE u.username = ? AND u.password = ? AND u.status = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final String role  = rs.getString("role");
                final int    uid   = rs.getInt("user_id");
                final String uname = rs.getString("username");
                final String email = rs.getString("email");
                updateLastLogin(uid);
                if ("CUSTOMER".equals(role)) {
                    return new Customer(uid, uname, email, role,
                            rs.getInt("customer_id"),
                            rs.getString("customer_code"),
                            rs.getString("first_name"),
                            rs.getString("last_name"));
                } else {
                    return new User(uid, uname, email, role) {
                        @Override
                        public void displayDashboard() {
                            if ("ADMIN".equals(role)) {
                                System.out.println("  1.  Manage Customers");
                                System.out.println("  2.  Manage Categories");
                                System.out.println("  3.  Manage Products");
                                System.out.println("  4.  View All Orders");
                                System.out.println("  5.  Update Order Status");
                                System.out.println("  6.  Manage Payments");
                                System.out.println("  7.  Manage Invoices");
                                System.out.println("  8.  Sales Reports");
                                System.out.println("  9.  Manage Users");
                                System.out.println("  10. System Logs");
                                System.out.println("  11. Logout");
                            } else {
                                System.out.println("  1.  Manage Customers");
                                System.out.println("  2.  Manage Categories");
                                System.out.println("  3.  Manage Products");
                                System.out.println("  4.  View All Orders");
                                System.out.println("  5.  Update Order Status");
                                System.out.println("  6.  Manage Payments");
                                System.out.println("  7.  Manage Invoices");
                                System.out.println("  8.  Sales Reports");
                                System.out.println("  9.  Logout");
                            }
                        }
                    };
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] authenticate: " + e.getMessage());
        }
        return null;
    }

    private void updateLastLogin(int uid) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET last_login = NOW() WHERE user_id = ?")) {
            ps.setInt(1, uid);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY user_id")) {
            while (rs.next()) {
                final String role = rs.getString("role");
                User u = new User(rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"), role) {
                    @Override public void displayDashboard() {}
                };
                u.setStatus(rs.getBoolean("status"));
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllUsers: " + e.getMessage());
        }
        return list;
    }

    public boolean createUser(String username, String password, String email, String role) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] createUser: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateUser(int userId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET status = FALSE WHERE user_id = ?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public void logAction(int userId, String action, String details) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO system_logs (user_id, action, details) VALUES (?, ?, ?)")) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void printSystemLogs() {
        String sql = "SELECT l.log_id, u.username, l.action, l.details, l.log_time "
                   + "FROM system_logs l LEFT JOIN users u ON l.user_id = u.user_id "
                   + "ORDER BY l.log_time DESC LIMIT 30";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("  %-5s %-15s %-25s %-30s %-20s%n",
                    "ID", "User", "Action", "Details", "Time");
            System.out.println("  " + "-".repeat(97));
            while (rs.next()) {
                String det = rs.getString("details");
                if (det != null && det.length() > 28) det = det.substring(0, 27) + "...";
                System.out.printf("  %-5d %-15s %-25s %-30s %-20s%n",
                        rs.getInt("log_id"), rs.getString("username"),
                        rs.getString("action"), det, rs.getString("log_time"));
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] printSystemLogs: " + e.getMessage());
        }
    }
}
