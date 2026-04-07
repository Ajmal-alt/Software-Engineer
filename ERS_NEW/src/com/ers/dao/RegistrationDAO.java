package com.ers.dao;

import com.ers.model.ExamSubject;
import com.ers.model.Registration;
import com.ers.model.Student;
import com.ers.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {
    private Connection conn;

    public RegistrationDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public int registerForExam(int studentId, int examId,
                                List<ExamSubject> subjects, double feePerSubject) {
        try {
            conn.setAutoCommit(false);
            // Check if already registered
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT registration_id FROM registrations "
                    + "WHERE student_id = ? AND exam_id = ? AND status != 'CANCELLED'")) {
                ps.setInt(1, studentId);
                ps.setInt(2, examId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    conn.setAutoCommit(true);
                    return -2;
                }
            }
            int totalSubjects = subjects.size();
            double totalFee   = totalSubjects * feePerSubject;
            String regNo      = "REG-" + (System.currentTimeMillis() % 1000000);

            int regId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO registrations "
                    + "(reg_number, student_id, exam_id, total_subjects, total_fee, status) "
                    + "VALUES (?, ?, ?, ?, ?, 'PENDING')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, regNo);
                ps.setInt(2,    studentId);
                ps.setInt(3,    examId);
                ps.setInt(4,    totalSubjects);
                ps.setDouble(5, totalFee);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return -1; }
                regId = rs.getInt(1);
            }
            for (ExamSubject es : subjects) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO registration_subjects "
                        + "(registration_id, exam_subject_id, subject_id, fee) "
                        + "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1,    regId);
                    ps.setInt(2,    es.getId());
                    ps.setInt(3,    es.getSubjectId());
                    ps.setDouble(4, feePerSubject);
                    ps.executeUpdate();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            return regId;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] registerForExam: " + e.getMessage());
            return -1;
        }
    }

    public boolean payFee(int registrationId, String paymentRef) {
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE registrations SET fee_paid = TRUE, payment_ref = ?, "
                    + "payment_date = NOW(), status = 'CONFIRMED' "
                    + "WHERE registration_id = ?")) {
                ps.setString(1, paymentRef);
                ps.setInt(2, registrationId);
                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] payFee: " + e.getMessage());
            return false;
        }
    }

    public boolean issueHallTicket(int registrationId) {
        String htNo = "HT-" + (System.currentTimeMillis() % 1000000);
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE registrations SET hall_ticket_no = ?, hall_ticket_issued = TRUE "
                + "WHERE registration_id = ? AND fee_paid = TRUE AND hall_ticket_issued = FALSE")) {
            ps.setString(1, htNo);
            ps.setInt(2, registrationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] issueHallTicket: " + e.getMessage());
            return false;
        }
    }

    public int issueHallTicketsBulk(int examId) {
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT registration_id FROM registrations "
                + "WHERE exam_id = ? AND fee_paid = TRUE "
                + "AND hall_ticket_issued = FALSE AND status = 'CONFIRMED'")) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (issueHallTicket(rs.getInt("registration_id"))) count++;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] issueHallTicketsBulk: " + e.getMessage());
        }
        return count;
    }

    public boolean updateRegistrationStatus(int registrationId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE registrations SET status = ? WHERE registration_id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, registrationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Registration getRegistrationById(int registrationId) {
        String sql = "SELECT r.*, CONCAT(s.first_name,' ',s.last_name) AS student_name, "
                   + "s.roll_number, e.exam_name "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN exams e ON r.exam_id = e.exam_id "
                   + "WHERE r.registration_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getRegistrationById: " + e.getMessage());
        }
        return null;
    }

    public List<Registration> getAllRegistrations() {
        List<Registration> list = new ArrayList<Registration>();
        String sql = "SELECT r.*, CONCAT(s.first_name,' ',s.last_name) AS student_name, "
                   + "s.roll_number, e.exam_name "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN exams e ON r.exam_id = e.exam_id "
                   + "ORDER BY r.registered_on DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllRegistrations: " + e.getMessage());
        }
        return list;
    }

    public List<Registration> getRegistrationsByStudent(int studentId) {
        List<Registration> list = new ArrayList<Registration>();
        String sql = "SELECT r.*, CONCAT(s.first_name,' ',s.last_name) AS student_name, "
                   + "s.roll_number, e.exam_name "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN exams e ON r.exam_id = e.exam_id "
                   + "WHERE r.student_id = ? ORDER BY r.registered_on DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getRegistrationsByStudent: " + e.getMessage());
        }
        return list;
    }

    public List<Registration> getRegistrationsByExam(int examId) {
        List<Registration> list = new ArrayList<Registration>();
        String sql = "SELECT r.*, CONCAT(s.first_name,' ',s.last_name) AS student_name, "
                   + "s.roll_number, e.exam_name "
                   + "FROM registrations r "
                   + "JOIN students s ON r.student_id = s.student_id "
                   + "JOIN exams e ON r.exam_id = e.exam_id "
                   + "WHERE r.exam_id = ? ORDER BY s.roll_number";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getRegistrationsByExam: " + e.getMessage());
        }
        return list;
    }

    public List<ExamSubject> getSubjectsForRegistration(int registrationId, ExamDAO examDAO) {
        List<ExamSubject> list = new ArrayList<ExamSubject>();
        String sql = "SELECT es.id FROM registration_subjects rs "
                   + "JOIN exam_subjects es ON rs.exam_subject_id = es.id "
                   + "WHERE rs.registration_id = ? ORDER BY es.exam_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExamSubject es = examDAO.getExamSubjectById(rs.getInt("id"));
                if (es != null) list.add(es);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getSubjectsForRegistration: " + e.getMessage());
        }
        return list;
    }

    public void printHallTicket(int registrationId, ExamDAO examDAO, StudentDAO studentDAO) {
        Registration r = getRegistrationById(registrationId);
        if (r == null) { System.out.println("  Registration not found."); return; }
        if (!r.isHallTicketIssued()) {
            System.out.println("  Hall ticket not yet issued for this registration.");
            return;
        }
        Student s = studentDAO.getStudentById(r.getStudentId());
        System.out.println("\n" + "=".repeat(65));
        System.out.println("           EXAM REGISTRATION SYSTEM");
        System.out.println("                  HALL TICKET");
        System.out.println("=".repeat(65));
        System.out.printf("  Hall Ticket No : %-30s%n", nvl(r.getHallTicketNo()));
        System.out.printf("  Reg Number     : %-30s%n", nvl(r.getRegNumber()));
        System.out.printf("  Exam           : %-30s%n", nvl(r.getExamName()));
        System.out.println("-".repeat(65));
        if (s != null) {
            System.out.printf("  Student Name   : %-30s%n", s.getFullName());
            System.out.printf("  Roll Number    : %-30s%n", nvl(s.getRollNumber()));
            System.out.printf("  Department     : %-30s%n", nvl(s.getDepartment()));
            System.out.printf("  Course         : %-30s%n", nvl(s.getCourse()));
            System.out.printf("  Semester       : %-30d%n", s.getSemester());
        }
        System.out.println("-".repeat(65));
        System.out.println("  EXAM SCHEDULE:");
        System.out.printf("  %-12s %-32s %-10s %-12s%n", "Date", "Subject", "Time", "Venue");
        System.out.println("  " + "-".repeat(68));
        List<ExamSubject> schedule = getSubjectsForRegistration(registrationId, examDAO);
        for (ExamSubject es : schedule) {
            String subj = es.getSubjectName().length() > 30
                    ? es.getSubjectName().substring(0, 29) + "." : es.getSubjectName();
            System.out.printf("  %-12s %-32s %-10s %-12s%n",
                    nvl(es.getExamDate()), subj, nvl(es.getExamTime()), nvl(es.getVenue()));
        }
        System.out.println("=".repeat(65));
        System.out.println("  NOTE: Bring this hall ticket and a valid photo ID to the exam.");
        System.out.println("=".repeat(65));
    }

    private Registration map(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setRegistrationId(rs.getInt("registration_id"));
        r.setRegNumber(rs.getString("reg_number"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setRollNumber(rs.getString("roll_number"));
        r.setExamId(rs.getInt("exam_id"));
        r.setExamName(rs.getString("exam_name"));
        r.setRegisteredOn(rs.getString("registered_on"));
        r.setTotalSubjects(rs.getInt("total_subjects"));
        r.setTotalFee(rs.getDouble("total_fee"));
        r.setFeePaid(rs.getBoolean("fee_paid"));
        r.setPaymentRef(rs.getString("payment_ref"));
        r.setPaymentDate(rs.getString("payment_date"));
        r.setHallTicketNo(rs.getString("hall_ticket_no"));
        r.setHallTicketIssued(rs.getBoolean("hall_ticket_issued"));
        r.setStatus(rs.getString("status"));
        r.setRemarks(rs.getString("remarks"));
        return r;
    }

    private String nvl(String s) { return s == null ? "-" : s; }
}
