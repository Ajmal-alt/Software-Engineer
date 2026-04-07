package com.rtms.dao;

import com.rtms.model.Driver;
import com.rtms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    private Connection conn;

    public DriverDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addDriver(Driver d, int createdBy) {
        try {
            conn.setAutoCommit(false);
            // Create user account
            String uSql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'DRIVER')";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(uSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, d.getUsername()); ps.setString(2, d.getPassword());
                ps.setString(3, d.getEmail()); ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return false; }
                userId = rs.getInt(1); d.setUserId(userId);
            }
            // Create driver profile (17 columns, 17 placeholders)
            String dSql = "INSERT INTO drivers "
                        + "(user_id, driver_code, first_name, last_name, date_of_birth, nationality, "
                        + " phone, email, license_number, license_grade, license_expiry, experience_years, "
                        + " contract_start, contract_end, salary, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
            try (PreparedStatement ps = conn.prepareStatement(dSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1,    userId);
                ps.setString(2, d.getDriverCode());
                ps.setString(3, d.getFirstName());
                ps.setString(4, d.getLastName());
                ps.setString(5, d.getDateOfBirth());
                ps.setString(6, d.getNationality());
                ps.setString(7, d.getPhone());
                ps.setString(8, d.getEmail());
                ps.setString(9, d.getLicenseNumber());
                ps.setString(10, d.getLicenseGrade());
                ps.setString(11, d.getLicenseExpiry());
                ps.setInt(12,   d.getExperienceYears());
                ps.setString(13, d.getContractStart());
                ps.setString(14, d.getContractEnd());
                ps.setDouble(15, d.getSalary());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) d.setDriverId(rs.getInt(1));
            }
            conn.commit(); conn.setAutoCommit(true); return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] addDriver: " + e.getMessage()); return false;
        }
    }

    public boolean updateDriverStatus(int driverId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE drivers SET status = ? WHERE driver_id = ?")) {
            ps.setString(1, status); ps.setInt(2, driverId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateContract(int driverId, String start, String end, double salary) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE drivers SET contract_start=?, contract_end=?, salary=? WHERE driver_id=?")) {
            ps.setString(1, start); ps.setString(2, end);
            ps.setDouble(3, salary); ps.setInt(4, driverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateStats(int driverId, int raceDelta, int winDelta,
                                int podiumDelta, int ptsDelta) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE drivers SET total_races=total_races+?, total_wins=total_wins+?, "
                + "total_podiums=total_podiums+?, championship_pts=championship_pts+? "
                + "WHERE driver_id=?")) {
            ps.setInt(1, raceDelta); ps.setInt(2, winDelta);
            ps.setInt(3, podiumDelta); ps.setInt(4, ptsDelta); ps.setInt(5, driverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public Driver getDriverById(int driverId) {
        String sql = "SELECT d.*, u.username, u.email AS user_email, u.role "
                   + "FROM drivers d JOIN users u ON d.user_id = u.user_id WHERE d.driver_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getDriverById: " + e.getMessage()); }
        return null;
    }

    public Driver getDriverByUserId(int userId) {
        String sql = "SELECT d.*, u.username, u.email AS user_email, u.role "
                   + "FROM drivers d JOIN users u ON d.user_id = u.user_id WHERE d.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getDriverByUserId: " + e.getMessage()); }
        return null;
    }

    public List<Driver> getAllDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT d.*, u.username, u.email AS user_email, u.role "
                   + "FROM drivers d JOIN users u ON d.user_id = u.user_id ORDER BY d.championship_pts DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllDrivers: " + e.getMessage()); }
        return list;
    }

    private Driver map(ResultSet rs) throws SQLException {
        Driver d = new Driver();
        d.setDriverId(rs.getInt("driver_id")); d.setUserId(rs.getInt("user_id"));
        d.setDriverCode(rs.getString("driver_code")); d.setFirstName(rs.getString("first_name"));
        d.setLastName(rs.getString("last_name")); d.setDateOfBirth(rs.getString("date_of_birth"));
        d.setNationality(rs.getString("nationality")); d.setPhone(rs.getString("phone"));
        d.setLicenseNumber(rs.getString("license_number")); d.setLicenseGrade(rs.getString("license_grade"));
        d.setLicenseExpiry(rs.getString("license_expiry")); d.setExperienceYears(rs.getInt("experience_years"));
        d.setTotalRaces(rs.getInt("total_races")); d.setTotalWins(rs.getInt("total_wins"));
        d.setTotalPodiums(rs.getInt("total_podiums")); d.setChampionshipPts(rs.getInt("championship_pts"));
        d.setContractStart(rs.getString("contract_start")); d.setContractEnd(rs.getString("contract_end"));
        d.setSalary(rs.getDouble("salary")); d.setDriverStatus(rs.getString("status"));
        d.setUsername(rs.getString("username")); d.setEmail(rs.getString("user_email"));
        d.setRole(rs.getString("role"));
        return d;
    }
}
