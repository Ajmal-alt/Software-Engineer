package com.rtms.dao;

import com.rtms.model.Staff;
import com.rtms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private Connection conn;

    public StaffDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addStaff(Staff s, int createdBy) {
        String sql = "INSERT INTO staff "
                   + "(staff_code, first_name, last_name, role_title, department, "
                   + " phone, email, nationality, contract_start, contract_end, salary, status, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStaffCode());   ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());    ps.setString(4, s.getRoleTitle());
            ps.setString(5, s.getDepartment());  ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());       ps.setString(8, s.getNationality());
            ps.setString(9, s.getContractStart()); ps.setString(10, s.getContractEnd());
            ps.setDouble(11, s.getSalary());     ps.setInt(12, createdBy);
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) s.setStaffId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.out.println("[DB ERROR] addStaff: " + e.getMessage()); }
        return false;
    }

    public boolean updateStaffStatus(int staffId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE staff SET status = ? WHERE staff_id = ?")) {
            ps.setString(1, status); ps.setInt(2, staffId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM staff ORDER BY department, staff_id")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllStaff: " + e.getMessage()); }
        return list;
    }

    public List<Staff> getStaffByDepartment(String department) {
        List<Staff> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM staff WHERE department = ? ORDER BY staff_id")) {
            ps.setString(1, department); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getStaffByDepartment: " + e.getMessage()); }
        return list;
    }

    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getInt("staff_id")); s.setStaffCode(rs.getString("staff_code"));
        s.setFirstName(rs.getString("first_name")); s.setLastName(rs.getString("last_name"));
        s.setRoleTitle(rs.getString("role_title")); s.setDepartment(rs.getString("department"));
        s.setPhone(rs.getString("phone")); s.setEmail(rs.getString("email"));
        s.setNationality(rs.getString("nationality")); s.setContractStart(rs.getString("contract_start"));
        s.setContractEnd(rs.getString("contract_end")); s.setSalary(rs.getDouble("salary"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
