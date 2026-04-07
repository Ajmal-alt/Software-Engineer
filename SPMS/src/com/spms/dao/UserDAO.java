package com.spms.dao;

import com.spms.model.*;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Authenticate and return typed User object */
    public User authenticate(String username, String password) {
        String sql = "SELECT u.*, e.employee_id, e.employee_code, e.first_name, " +
                     "e.last_name, e.designation FROM users u " +
                     "LEFT JOIN employees e ON u.user_id = e.user_id " +
                     "WHERE u.username=? AND u.password=? AND u.status=TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role    = rs.getString("role");
                int    userId  = rs.getInt("user_id");
                String uname   = rs.getString("username");
                String email   = rs.getString("email");

                // Update last_login
                updateLastLogin(userId);

                if ("EMPLOYEE".equals(role) || "PROJECT_MANAGER".equals(role)) {
                    Employee emp = new Employee(userId, uname, email, role,
                            rs.getInt("employee_id"),
                            rs.getString("employee_code"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("designation"));
                    return emp;
                } else {
                    // Admin / HR – return anonymous subclass
                    final String r = role;
                    return new User(userId, uname, email, role) {
                        @Override public void displayDashboard() {
                            if ("ADMIN".equals(r)) {
                                System.out.println("  1. Manage Users");
                                System.out.println("  2. Manage Roles");
                                System.out.println("  3. View System Logs");
                                System.out.println("  4. Configure System");
                                System.out.println("  5. Manage Departments");
                                System.out.println("  6. Logout");
                            } else {
                                System.out.println("  1. Add Employee");
                                System.out.println("  2. View All Employees");
                                System.out.println("  3. Deactivate Employee");
                                System.out.println("  4. Process Payroll");
                                System.out.println("  5. Generate Reports");
                                System.out.println("  6. Manage Departments");
                                System.out.println("  7. Logout");
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

    private void updateLastLogin(int userId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET last_login=NOW() WHERE user_id=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) user.setUserId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] createUser: " + e.getMessage());
        }
        return false;
    }

    public boolean deactivateUser(int userId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET status=FALSE WHERE user_id=?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] deactivateUser: " + e.getMessage());
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT user_id, username, email, role, status, last_login FROM users ORDER BY user_id")) {
            while (rs.next()) {
                final String role = rs.getString("role");
                User u = new User(rs.getInt("user_id"), rs.getString("username"),
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

    public void logAction(int userId, String action, String details) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO system_logs (user_id, action, details) VALUES (?,?,?)")) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void printSystemLogs() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT l.log_id, u.username, l.action, l.details, l.log_time " +
                "FROM system_logs l LEFT JOIN users u ON l.user_id=u.user_id " +
                "ORDER BY l.log_time DESC LIMIT 30")) {
            System.out.printf("%-5s %-15s %-25s %-30s %-20s%n", "ID","User","Action","Details","Time");
            System.out.println("-".repeat(98));
            while (rs.next()) {
                System.out.printf("%-5d %-15s %-25s %-30s %-20s%n",
                    rs.getInt("log_id"),
                    rs.getString("username"),
                    rs.getString("action"),
                    truncate(rs.getString("details"), 28),
                    rs.getString("log_time"));
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}
