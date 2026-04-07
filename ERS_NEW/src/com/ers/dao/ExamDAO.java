package com.ers.dao;

import com.ers.model.Exam;
import com.ers.model.ExamSubject;
import com.ers.model.Subject;
import com.ers.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {
    private Connection conn;

    public ExamDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── Subjects ──────────────────────────────────────────────────────────────

    public boolean addSubject(Subject s) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, department, credits, subject_type, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDepartment());
            ps.setInt(4,    s.getCredits());
            ps.setString(5, s.getSubjectType());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) s.setSubjectId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addSubject: " + e.getMessage());
        }
        return false;
    }

    public boolean updateSubjectStatus(int subjectId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE subjects SET status = ? WHERE subject_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, subjectId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<Subject>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM subjects ORDER BY department, subject_code")) {
            while (rs.next()) list.add(mapSubject(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllSubjects: " + e.getMessage());
        }
        return list;
    }

    public Subject getSubjectById(int subjectId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM subjects WHERE subject_id = ?")) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSubject(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getSubjectById: " + e.getMessage());
        }
        return null;
    }

    // ── Exams ─────────────────────────────────────────────────────────────────

    public boolean createExam(Exam e, int createdBy) {
        String sql = "INSERT INTO exams "
                   + "(exam_code, exam_name, exam_type, academic_year, semester, department, "
                   + " reg_start_date, reg_end_date, exam_start_date, exam_end_date, "
                   + " fee_per_subject, max_subjects, status, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'UPCOMING', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  e.getExamCode());
            ps.setString(2,  e.getExamName());
            ps.setString(3,  e.getExamType());
            ps.setString(4,  e.getAcademicYear());
            ps.setInt(5,     e.getSemester());
            ps.setString(6,  e.getDepartment());
            ps.setString(7,  e.getRegStartDate());
            ps.setString(8,  e.getRegEndDate());
            ps.setString(9,  e.getExamStartDate());
            ps.setString(10, e.getExamEndDate());
            ps.setDouble(11, e.getFeePerSubject());
            ps.setInt(12,    e.getMaxSubjects());
            ps.setInt(13,    createdBy);
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) e.setExamId(rs.getInt(1));
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("[DB ERROR] createExam: " + ex.getMessage());
        }
        return false;
    }

    public boolean updateExamStatus(int examId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE exams SET status = ? WHERE exam_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Exam getExamById(int examId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM exams WHERE exam_id = ?")) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapExam(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getExamById: " + e.getMessage());
        }
        return null;
    }

    public List<Exam> getAllExams() {
        List<Exam> list = new ArrayList<Exam>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM exams ORDER BY exam_id")) {
            while (rs.next()) list.add(mapExam(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllExams: " + e.getMessage());
        }
        return list;
    }

    public List<Exam> getOpenExams() {
        List<Exam> list = new ArrayList<Exam>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM exams WHERE status = 'REGISTRATION_OPEN' "
                     + "AND CURDATE() BETWEEN reg_start_date AND reg_end_date "
                     + "ORDER BY exam_id")) {
            while (rs.next()) list.add(mapExam(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getOpenExams: " + e.getMessage());
        }
        return list;
    }

    // ── Exam Schedule ─────────────────────────────────────────────────────────

    public boolean addExamSubject(ExamSubject es) {
        String sql = "INSERT INTO exam_subjects "
                   + "(exam_id, subject_id, exam_date, exam_time, duration_mins, max_marks, pass_marks, venue) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    es.getExamId());
            ps.setInt(2,    es.getSubjectId());
            ps.setString(3, es.getExamDate());
            ps.setString(4, es.getExamTime());
            ps.setInt(5,    es.getDurationMins());
            ps.setInt(6,    es.getMaxMarks());
            ps.setInt(7,    es.getPassMarks());
            ps.setString(8, es.getVenue());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) es.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addExamSubject: " + e.getMessage());
        }
        return false;
    }

    public List<ExamSubject> getExamSchedule(int examId) {
        List<ExamSubject> list = new ArrayList<ExamSubject>();
        String sql = "SELECT es.*, s.subject_code, s.subject_name, e.exam_name "
                   + "FROM exam_subjects es "
                   + "JOIN subjects s ON es.subject_id = s.subject_id "
                   + "JOIN exams e ON es.exam_id = e.exam_id "
                   + "WHERE es.exam_id = ? ORDER BY es.exam_date, es.exam_time";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapExamSubject(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getExamSchedule: " + e.getMessage());
        }
        return list;
    }

    public ExamSubject getExamSubjectById(int id) {
        String sql = "SELECT es.*, s.subject_code, s.subject_name, e.exam_name "
                   + "FROM exam_subjects es "
                   + "JOIN subjects s ON es.subject_id = s.subject_id "
                   + "JOIN exams e ON es.exam_id = e.exam_id "
                   + "WHERE es.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapExamSubject(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getExamSubjectById: " + e.getMessage());
        }
        return null;
    }

    private Subject mapSubject(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setSubjectId(rs.getInt("subject_id"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setDepartment(rs.getString("department"));
        s.setCredits(rs.getInt("credits"));
        s.setSubjectType(rs.getString("subject_type"));
        s.setStatus(rs.getString("status"));
        return s;
    }

    private Exam mapExam(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setExamId(rs.getInt("exam_id"));
        e.setExamCode(rs.getString("exam_code"));
        e.setExamName(rs.getString("exam_name"));
        e.setExamType(rs.getString("exam_type"));
        e.setAcademicYear(rs.getString("academic_year"));
        e.setSemester(rs.getInt("semester"));
        e.setDepartment(rs.getString("department"));
        e.setRegStartDate(rs.getString("reg_start_date"));
        e.setRegEndDate(rs.getString("reg_end_date"));
        e.setExamStartDate(rs.getString("exam_start_date"));
        e.setExamEndDate(rs.getString("exam_end_date"));
        e.setFeePerSubject(rs.getDouble("fee_per_subject"));
        e.setMaxSubjects(rs.getInt("max_subjects"));
        e.setStatus(rs.getString("status"));
        return e;
    }

    private ExamSubject mapExamSubject(ResultSet rs) throws SQLException {
        ExamSubject es = new ExamSubject();
        es.setId(rs.getInt("id"));
        es.setExamId(rs.getInt("exam_id"));
        es.setExamName(rs.getString("exam_name"));
        es.setSubjectId(rs.getInt("subject_id"));
        es.setSubjectCode(rs.getString("subject_code"));
        es.setSubjectName(rs.getString("subject_name"));
        es.setExamDate(rs.getString("exam_date"));
        es.setExamTime(rs.getString("exam_time"));
        es.setDurationMins(rs.getInt("duration_mins"));
        es.setMaxMarks(rs.getInt("max_marks"));
        es.setPassMarks(rs.getInt("pass_marks"));
        es.setVenue(rs.getString("venue"));
        return es;
    }
}
