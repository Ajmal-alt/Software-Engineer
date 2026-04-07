package com.spms.dao;

import com.spms.model.LeaveApplication;
import com.spms.model.LeaveBalance;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {
    private Connection conn;

    public LeaveDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean applyLeave(LeaveApplication la) {
        // Check balance first
        LeaveBalance bal = getBalance(la.getEmployeeId(), la.getLeaveTypeId());
        if (bal == null || bal.getRemainingLeaves() < la.getTotalDays()) return false;

        String sql = "INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason) " +
                     "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, la.getEmployeeId());
            ps.setInt(2, la.getLeaveTypeId());
            ps.setString(3, la.getStartDate());
            ps.setString(4, la.getEndDate());
            ps.setInt(5, la.getTotalDays());
            ps.setString(6, la.getReason());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) la.setLeaveId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] applyLeave: " + e.getMessage());
        }
        return false;
    }

    public boolean approveLeave(int leaveId, int approvedBy) {
        try {
            conn.setAutoCommit(false);
            // Fetch leave details
            LeaveApplication la = getLeaveById(leaveId);
            if (la == null || !"PENDING".equals(la.getStatus())) {
                conn.setAutoCommit(true); return false;
            }
            // Update status
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE leave_applications SET status='APPROVED', approved_by=?, approved_on=NOW() WHERE leave_id=?")) {
                ps.setInt(1, approvedBy); ps.setInt(2, leaveId);
                ps.executeUpdate();
            }
            // Deduct balance
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE leave_balance SET used_leaves=used_leaves+?, remaining_leaves=remaining_leaves-? " +
                    "WHERE employee_id=? AND leave_type_id=? AND year=YEAR(CURDATE())")) {
                ps.setInt(1, la.getTotalDays()); ps.setInt(2, la.getTotalDays());
                ps.setInt(3, la.getEmployeeId()); ps.setInt(4, la.getLeaveTypeId());
                ps.executeUpdate();
            }
            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] approveLeave: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectLeave(int leaveId, int rejectedBy, String comments) {
        String sql = "UPDATE leave_applications SET status='REJECTED', approved_by=?, approved_on=NOW(), comments=? WHERE leave_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rejectedBy); ps.setString(2, comments); ps.setInt(3, leaveId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] rejectLeave: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelLeave(int leaveId, int employeeId) {
        String sql = "UPDATE leave_applications SET status='CANCELLED' WHERE leave_id=? AND employee_id=? AND status='PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leaveId); ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] cancelLeave: " + e.getMessage());
            return false;
        }
    }

    public List<LeaveApplication> getLeavesByEmployee(int employeeId) {
        List<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT la.*, lt.leave_type_name, CONCAT(e.first_name,' ',e.last_name) AS emp_name " +
                     "FROM leave_applications la " +
                     "JOIN leave_types lt ON la.leave_type_id=lt.leave_type_id " +
                     "JOIN employees e ON la.employee_id=e.employee_id " +
                     "WHERE la.employee_id=? ORDER BY la.applied_on DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapLeave(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getLeavesByEmployee: " + e.getMessage());
        }
        return list;
    }

    public List<LeaveApplication> getPendingLeaves() {
        List<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT la.*, lt.leave_type_name, CONCAT(e.first_name,' ',e.last_name) AS emp_name " +
                     "FROM leave_applications la " +
                     "JOIN leave_types lt ON la.leave_type_id=lt.leave_type_id " +
                     "JOIN employees e ON la.employee_id=e.employee_id " +
                     "WHERE la.status='PENDING' ORDER BY la.applied_on";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapLeave(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getPendingLeaves: " + e.getMessage());
        }
        return list;
    }

    public LeaveApplication getLeaveById(int leaveId) {
        String sql = "SELECT la.*, lt.leave_type_name, CONCAT(e.first_name,' ',e.last_name) AS emp_name " +
                     "FROM leave_applications la " +
                     "JOIN leave_types lt ON la.leave_type_id=lt.leave_type_id " +
                     "JOIN employees e ON la.employee_id=e.employee_id " +
                     "WHERE la.leave_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leaveId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapLeave(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getLeaveById: " + e.getMessage());
        }
        return null;
    }

    public LeaveBalance getBalance(int employeeId, int leaveTypeId) {
        String sql = "SELECT lb.*, lt.leave_type_name FROM leave_balance lb " +
                     "JOIN leave_types lt ON lb.leave_type_id=lt.leave_type_id " +
                     "WHERE lb.employee_id=? AND lb.leave_type_id=? AND lb.year=YEAR(CURDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId); ps.setInt(2, leaveTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapBalance(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getBalance: " + e.getMessage());
        }
        return null;
    }

    public List<LeaveBalance> getAllBalances(int employeeId) {
        List<LeaveBalance> list = new ArrayList<>();
        String sql = "SELECT lb.*, lt.leave_type_name FROM leave_balance lb " +
                     "JOIN leave_types lt ON lb.leave_type_id=lt.leave_type_id " +
                     "WHERE lb.employee_id=? AND lb.year=YEAR(CURDATE()) ORDER BY lb.leave_type_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBalance(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllBalances: " + e.getMessage());
        }
        return list;
    }

    public List<String[]> getLeaveTypes() {
        List<String[]> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT leave_type_id, leave_type_name FROM leave_types ORDER BY leave_type_id")) {
            while (rs.next()) list.add(new String[]{rs.getString("leave_type_id"), rs.getString("leave_type_name")});
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getLeaveTypes: " + e.getMessage());
        }
        return list;
    }

    private LeaveApplication mapLeave(ResultSet rs) throws SQLException {
        LeaveApplication la = new LeaveApplication();
        la.setLeaveId(rs.getInt("leave_id"));
        la.setEmployeeId(rs.getInt("employee_id"));
        la.setEmployeeName(rs.getString("emp_name"));
        la.setLeaveTypeId(rs.getInt("leave_type_id"));
        la.setLeaveTypeName(rs.getString("leave_type_name"));
        la.setStartDate(rs.getString("start_date"));
        la.setEndDate(rs.getString("end_date"));
        la.setTotalDays(rs.getInt("total_days"));
        la.setReason(rs.getString("reason"));
        la.setStatus(rs.getString("status"));
        la.setAppliedOn(rs.getString("applied_on"));
        la.setComments(rs.getString("comments"));
        return la;
    }

    private LeaveBalance mapBalance(ResultSet rs) throws SQLException {
        LeaveBalance lb = new LeaveBalance();
        lb.setBalanceId(rs.getInt("balance_id"));
        lb.setEmployeeId(rs.getInt("employee_id"));
        lb.setLeaveTypeId(rs.getInt("leave_type_id"));
        lb.setLeaveTypeName(rs.getString("leave_type_name"));
        lb.setYear(rs.getInt("year"));
        lb.setTotalLeaves(rs.getInt("total_leaves"));
        lb.setUsedLeaves(rs.getInt("used_leaves"));
        lb.setRemainingLeaves(rs.getInt("remaining_leaves"));
        return lb;
    }
}
