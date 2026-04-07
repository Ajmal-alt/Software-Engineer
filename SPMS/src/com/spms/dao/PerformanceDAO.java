package com.spms.dao;

import com.spms.model.PerformanceReview;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {
    private Connection conn;

    public PerformanceDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addReview(PerformanceReview r) {
        String sql = "INSERT INTO performance_reviews (employee_id, reviewer_id, review_period, review_date, rating, comments, status) " +
                     "VALUES (?,?,?,?,?,?,'SUBMITTED')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getEmployeeId());
            ps.setInt(2, r.getReviewerId());
            ps.setString(3, r.getReviewPeriod());
            ps.setString(4, r.getReviewDate());
            ps.setDouble(5, r.getRating());
            ps.setString(6, r.getComments());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) r.setReviewId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addReview: " + e.getMessage());
        }
        return false;
    }

    public List<PerformanceReview> getReviewsByEmployee(int employeeId) {
        List<PerformanceReview> list = new ArrayList<>();
        String sql = "SELECT pr.*, " +
                     "CONCAT(e.first_name,' ',e.last_name) AS emp_name, " +
                     "CONCAT(r.first_name,' ',r.last_name) AS reviewer_name " +
                     "FROM performance_reviews pr " +
                     "JOIN employees e ON pr.employee_id=e.employee_id " +
                     "JOIN employees r ON pr.reviewer_id=r.employee_id " +
                     "WHERE pr.employee_id=? ORDER BY pr.review_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapReview(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getReviewsByEmployee: " + e.getMessage());
        }
        return list;
    }

    public List<PerformanceReview> getReviewsByReviewer(int reviewerId) {
        List<PerformanceReview> list = new ArrayList<>();
        String sql = "SELECT pr.*, " +
                     "CONCAT(e.first_name,' ',e.last_name) AS emp_name, " +
                     "CONCAT(r.first_name,' ',r.last_name) AS reviewer_name " +
                     "FROM performance_reviews pr " +
                     "JOIN employees e ON pr.employee_id=e.employee_id " +
                     "JOIN employees r ON pr.reviewer_id=r.employee_id " +
                     "WHERE pr.reviewer_id=? ORDER BY pr.review_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapReview(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getReviewsByReviewer: " + e.getMessage());
        }
        return list;
    }

    public List<PerformanceReview> getAllReviews() {
        List<PerformanceReview> list = new ArrayList<>();
        String sql = "SELECT pr.*, " +
                     "CONCAT(e.first_name,' ',e.last_name) AS emp_name, " +
                     "CONCAT(r.first_name,' ',r.last_name) AS reviewer_name " +
                     "FROM performance_reviews pr " +
                     "JOIN employees e ON pr.employee_id=e.employee_id " +
                     "JOIN employees r ON pr.reviewer_id=r.employee_id " +
                     "ORDER BY pr.review_date DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapReview(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllReviews: " + e.getMessage());
        }
        return list;
    }

    private PerformanceReview mapReview(ResultSet rs) throws SQLException {
        PerformanceReview r = new PerformanceReview();
        r.setReviewId(rs.getInt("review_id"));
        r.setEmployeeId(rs.getInt("employee_id"));
        r.setEmployeeName(rs.getString("emp_name"));
        r.setReviewerId(rs.getInt("reviewer_id"));
        r.setReviewerName(rs.getString("reviewer_name"));
        r.setReviewPeriod(rs.getString("review_period"));
        r.setReviewDate(rs.getString("review_date"));
        r.setRating(rs.getDouble("rating"));
        r.setComments(rs.getString("comments"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
