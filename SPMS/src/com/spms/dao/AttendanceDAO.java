package com.spms.dao;

import com.spms.model.Attendance;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private Connection conn;

    public AttendanceDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Check-in: create attendance record for today */
    public boolean checkIn(int employeeId) {
        // Prevent duplicate check-in
        if (getTodayAttendance(employeeId) != null) return false;
        String sql = "INSERT INTO attendance (employee_id, attendance_date, check_in_time, status) " +
                     "VALUES (?, CURDATE(), CURTIME(), 'PRESENT')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] checkIn: " + e.getMessage());
            return false;
        }
    }

    /** Check-out: fill check_out_time and total_hours */
    public boolean checkOut(int employeeId) {
        Attendance today = getTodayAttendance(employeeId);
        if (today == null || today.getCheckOutTime() != null) return false;
        String sql = "UPDATE attendance SET check_out_time=CURTIME(), " +
                     "total_hours=TIMESTAMPDIFF(MINUTE, check_in_time, CURTIME())/60.0 " +
                     "WHERE employee_id=? AND attendance_date=CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] checkOut: " + e.getMessage());
            return false;
        }
    }

    public Attendance getTodayAttendance(int employeeId) {
        String sql = "SELECT * FROM attendance WHERE employee_id=? AND attendance_date=CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAttendance(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getTodayAttendance: " + e.getMessage());
        }
        return null;
    }

    public List<Attendance> getMonthlyAttendance(int employeeId, int month, int year) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE employee_id=? " +
                     "AND MONTH(attendance_date)=? AND YEAR(attendance_date)=? " +
                     "ORDER BY attendance_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId); ps.setInt(2, month); ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getMonthlyAttendance: " + e.getMessage());
        }
        return list;
    }

    /** Team attendance for a manager (all employees in their projects) */
    public List<Attendance> getTeamAttendanceToday(int managerId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(e.first_name,' ',e.last_name) AS emp_name " +
                     "FROM attendance a JOIN employees e ON a.employee_id=e.employee_id " +
                     "JOIN project_assignments pa ON e.employee_id=pa.employee_id " +
                     "JOIN projects p ON pa.project_id=p.project_id " +
                     "WHERE p.manager_id=? AND a.attendance_date=CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance a = mapAttendance(rs);
                a.setEmployeeName(rs.getString("emp_name"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getTeamAttendanceToday: " + e.getMessage());
        }
        return list;
    }

    public int countPresentDays(int employeeId, int month, int year) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE employee_id=? " +
                     "AND MONTH(attendance_date)=? AND YEAR(attendance_date)=? " +
                     "AND status IN ('PRESENT','HALF_DAY')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId); ps.setInt(2, month); ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] countPresentDays: " + e.getMessage());
        }
        return 0;
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setEmployeeId(rs.getInt("employee_id"));
        a.setAttendanceDate(rs.getString("attendance_date"));
        a.setCheckInTime(rs.getString("check_in_time"));
        a.setCheckOutTime(rs.getString("check_out_time"));
        a.setTotalHours(rs.getDouble("total_hours"));
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        return a;
    }
}
