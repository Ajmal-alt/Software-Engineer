package com.ers.dao;

import com.ers.model.Student;
import com.ers.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection conn;

    public StudentDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addStudent(Student s) {
        try {
            conn.setAutoCommit(false);
            // Step 1: create user account
            String uSql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'STUDENT')";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(uSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getUsername());
                ps.setString(2, s.getPassword());
                ps.setString(3, s.getEmail());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return false; }
                userId = rs.getInt(1);
                s.setUserId(userId);
            }
            // Step 2: create student profile
            // 17 columns listed, 17 placeholders + 1 literal = correct
            String sSql = "INSERT INTO students "
                        + "(user_id, student_code, first_name, last_name, date_of_birth, gender, "
                        + " phone, email, address, city, state, pincode, "
                        + " department, course, semester, roll_number, year_of_admission, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
            try (PreparedStatement ps = conn.prepareStatement(sSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1,     userId);
                ps.setString(2,  s.getStudentCode());
                ps.setString(3,  s.getFirstName());
                ps.setString(4,  s.getLastName());
                ps.setString(5,  s.getDateOfBirth());
                ps.setString(6,  s.getGender());
                ps.setString(7,  s.getPhone());
                ps.setString(8,  s.getEmail());
                ps.setString(9,  s.getAddress());
                ps.setString(10, s.getCity());
                ps.setString(11, s.getState());
                ps.setString(12, s.getPincode());
                ps.setString(13, s.getDepartment());
                ps.setString(14, s.getCourse());
                ps.setInt(15,    s.getSemester());
                ps.setString(16, s.getRollNumber());
                ps.setInt(17,    s.getYearOfAdmission());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) s.setStudentId(rs.getInt(1));
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] addStudent: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudentStatus(int studentId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE students SET status = ? WHERE student_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateSemester(int studentId, int semester) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE students SET semester = ? WHERE student_id = ?")) {
            ps.setInt(1, semester);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT s.*, u.username, u.email AS user_email, u.role "
                   + "FROM students s JOIN users u ON s.user_id = u.user_id "
                   + "WHERE s.student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getStudentById: " + e.getMessage());
        }
        return null;
    }

    public Student getStudentByUserId(int userId) {
        String sql = "SELECT s.*, u.username, u.email AS user_email, u.role "
                   + "FROM students s JOIN users u ON s.user_id = u.user_id "
                   + "WHERE s.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getStudentByUserId: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<Student>();
        String sql = "SELECT s.*, u.username, u.email AS user_email, u.role "
                   + "FROM students s JOIN users u ON s.user_id = u.user_id "
                   + "ORDER BY s.student_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllStudents: " + e.getMessage());
        }
        return list;
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setStudentCode(rs.getString("student_code"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setDateOfBirth(rs.getString("date_of_birth"));
        s.setGender(rs.getString("gender"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        s.setCity(rs.getString("city"));
        s.setState(rs.getString("state"));
        s.setPincode(rs.getString("pincode"));
        s.setDepartment(rs.getString("department"));
        s.setCourse(rs.getString("course"));
        s.setSemester(rs.getInt("semester"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setYearOfAdmission(rs.getInt("year_of_admission"));
        s.setStudentStatus(rs.getString("status"));
        s.setUsername(rs.getString("username"));
        s.setEmail(rs.getString("user_email"));
        s.setRole(rs.getString("role"));
        return s;
    }
}
