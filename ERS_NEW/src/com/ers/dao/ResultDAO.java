package com.ers.dao;

import com.ers.model.Result;
import com.ers.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {
    private Connection conn;

    public ResultDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    private String calculateGrade(double marks, int maxMarks) {
        double pct = (marks / maxMarks) * 100.0;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }

    public boolean enterResult(int registrationId, int studentId, int examId,
                                int subjectId, double marks, int maxMarks,
                                int passMarks, int enteredBy) {
        String grade  = calculateGrade(marks, maxMarks);
        String status = (marks >= passMarks) ? "PASS" : "FAIL";
        String sql = "INSERT INTO results "
                   + "(registration_id, student_id, exam_id, subject_id, marks_obtained, "
                   + " max_marks, pass_marks, grade, result_status, published, entered_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE, ?) "
                   + "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained), "
                   + "grade = VALUES(grade), result_status = VALUES(result_status), "
                   + "entered_by = VALUES(entered_by)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    registrationId);
            ps.setInt(2,    studentId);
            ps.setInt(3,    examId);
            ps.setInt(4,    subjectId);
            ps.setDouble(5, marks);
            ps.setInt(6,    maxMarks);
            ps.setInt(7,    passMarks);
            ps.setString(8, grade);
            ps.setString(9, status);
            ps.setInt(10,   enteredBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] enterResult: " + e.getMessage());
            return false;
        }
    }

    public boolean markAbsent(int registrationId, int studentId, int examId,
                               int subjectId, int maxMarks, int passMarks, int enteredBy) {
        String sql = "INSERT INTO results "
                   + "(registration_id, student_id, exam_id, subject_id, marks_obtained, "
                   + " max_marks, pass_marks, grade, result_status, published, entered_by) "
                   + "VALUES (?, ?, ?, ?, 0, ?, ?, 'AB', 'ABSENT', FALSE, ?) "
                   + "ON DUPLICATE KEY UPDATE result_status = 'ABSENT', grade = 'AB', marks_obtained = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ps.setInt(2, studentId);
            ps.setInt(3, examId);
            ps.setInt(4, subjectId);
            ps.setInt(5, maxMarks);
            ps.setInt(6, passMarks);
            ps.setInt(7, enteredBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] markAbsent: " + e.getMessage());
            return false;
        }
    }

    public int publishResults(int examId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE results SET published = TRUE "
                + "WHERE exam_id = ? AND published = FALSE")) {
            ps.setInt(1, examId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] publishResults: " + e.getMessage());
            return 0;
        }
    }

    public List<Result> getResultsByRegistration(int registrationId) {
        List<Result> list = new ArrayList<Result>();
        String sql = "SELECT r.*, CONCAT(st.first_name,' ',st.last_name) AS student_name, "
                   + "st.roll_number, s.subject_code, s.subject_name, e.exam_name "
                   + "FROM results r "
                   + "JOIN students st ON r.student_id = st.student_id "
                   + "JOIN subjects s  ON r.subject_id = s.subject_id "
                   + "JOIN exams e     ON r.exam_id    = e.exam_id "
                   + "WHERE r.registration_id = ? ORDER BY s.subject_code";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getResultsByRegistration: " + e.getMessage());
        }
        return list;
    }

    public List<Result> getResultsByExam(int examId) {
        List<Result> list = new ArrayList<Result>();
        String sql = "SELECT r.*, CONCAT(st.first_name,' ',st.last_name) AS student_name, "
                   + "st.roll_number, s.subject_code, s.subject_name, e.exam_name "
                   + "FROM results r "
                   + "JOIN students st ON r.student_id = st.student_id "
                   + "JOIN subjects s  ON r.subject_id = s.subject_id "
                   + "JOIN exams e     ON r.exam_id    = e.exam_id "
                   + "WHERE r.exam_id = ? AND r.published = TRUE "
                   + "ORDER BY st.roll_number, s.subject_code";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getResultsByExam: " + e.getMessage());
        }
        return list;
    }

    public List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<Result>();
        String sql = "SELECT r.*, CONCAT(st.first_name,' ',st.last_name) AS student_name, "
                   + "st.roll_number, s.subject_code, s.subject_name, e.exam_name "
                   + "FROM results r "
                   + "JOIN students st ON r.student_id = st.student_id "
                   + "JOIN subjects s  ON r.subject_id = s.subject_id "
                   + "JOIN exams e     ON r.exam_id    = e.exam_id "
                   + "WHERE r.student_id = ? AND r.published = TRUE "
                   + "ORDER BY e.exam_id, s.subject_code";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getResultsByStudent: " + e.getMessage());
        }
        return list;
    }

    public void printMarksheet(int registrationId) {
        List<Result> results = getResultsByRegistration(registrationId);
        if (results.isEmpty()) {
            System.out.println("  No results found for this registration.");
            return;
        }
        Result first = results.get(0);
        System.out.println("\n" + "=".repeat(65));
        System.out.println("           EXAM REGISTRATION SYSTEM");
        System.out.println("               MARKSHEET / RESULT");
        System.out.println("=".repeat(65));
        System.out.printf("  Student  : %-35s%n", first.getStudentName());
        System.out.printf("  Roll No  : %-35s%n", nvl(first.getRollNumber()));
        System.out.printf("  Exam     : %-35s%n", first.getExamName());
        System.out.println("-".repeat(65));
        System.out.printf("  %-8s %-28s %-5s %-5s %-6s %-6s %-8s%n",
                "Code", "Subject", "Max", "Pass", "Got", "Grade", "Status");
        System.out.println("  " + "-".repeat(65));
        double totalMarks = 0.0;
        int    totalMax   = 0;
        int    failCount  = 0;
        for (Result r : results) {
            String subj = r.getSubjectName().length() > 26
                    ? r.getSubjectName().substring(0, 25) + "." : r.getSubjectName();
            System.out.printf("  %-8s %-28s %-5d %-5d %-6.1f %-6s %-8s%n",
                    r.getSubjectCode(), subj, r.getMaxMarks(), r.getPassMarks(),
                    r.getMarksObtained(), r.getGrade(), r.getResultStatus());
            totalMarks += r.getMarksObtained();
            totalMax   += r.getMaxMarks();
            if ("FAIL".equals(r.getResultStatus()) || "ABSENT".equals(r.getResultStatus())) {
                failCount++;
            }
        }
        System.out.println("  " + "-".repeat(65));
        double pct = (totalMax > 0) ? (totalMarks * 100.0 / totalMax) : 0.0;
        System.out.printf("  %-44s %.1f / %d  (%.2f%%)%n", "Total:", totalMarks, totalMax, pct);
        String overall = (failCount == 0)
                ? "*** PASS ***"
                : "*** FAIL (" + failCount + " subject(s) failed) ***";
        System.out.printf("  %-44s %s%n", "Overall Result:", overall);
        System.out.println("=".repeat(65));
    }

    public void printExamResultSummary(int examId) {
        String sql = "SELECT s.subject_code, s.subject_name, "
                   + "COUNT(*) AS total, "
                   + "SUM(CASE WHEN r.result_status = 'PASS'   THEN 1 ELSE 0 END) AS passes, "
                   + "SUM(CASE WHEN r.result_status = 'FAIL'   THEN 1 ELSE 0 END) AS fails, "
                   + "SUM(CASE WHEN r.result_status = 'ABSENT' THEN 1 ELSE 0 END) AS absents, "
                   + "ROUND(AVG(CASE WHEN r.result_status != 'ABSENT' "
                   + "    THEN r.marks_obtained END), 2) AS avg_marks "
                   + "FROM results r "
                   + "JOIN subjects s ON r.subject_id = s.subject_id "
                   + "WHERE r.exam_id = ? "
                   + "GROUP BY r.subject_id, s.subject_code, s.subject_name "
                   + "ORDER BY s.subject_code";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-8s %-30s %-6s %-6s %-6s %-8s %-8s%n",
                    "Code", "Subject", "Total", "Pass", "Fail", "Absent", "Avg");
            System.out.println("  " + "-".repeat(75));
            while (rs.next()) {
                String subj = rs.getString("subject_name").length() > 28
                        ? rs.getString("subject_name").substring(0, 27) + "."
                        : rs.getString("subject_name");
                System.out.printf("  %-8s %-30s %-6d %-6d %-6d %-8d %-8.1f%n",
                        rs.getString("subject_code"), subj,
                        rs.getInt("total"), rs.getInt("passes"),
                        rs.getInt("fails"), rs.getInt("absents"),
                        rs.getDouble("avg_marks"));
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] printExamResultSummary: " + e.getMessage());
        }
    }

    private Result map(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setResultId(rs.getInt("result_id"));
        r.setRegistrationId(rs.getInt("registration_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setRollNumber(rs.getString("roll_number"));
        r.setExamId(rs.getInt("exam_id"));
        r.setExamName(rs.getString("exam_name"));
        r.setSubjectId(rs.getInt("subject_id"));
        r.setSubjectCode(rs.getString("subject_code"));
        r.setSubjectName(rs.getString("subject_name"));
        r.setMarksObtained(rs.getDouble("marks_obtained"));
        r.setMaxMarks(rs.getInt("max_marks"));
        r.setPassMarks(rs.getInt("pass_marks"));
        r.setGrade(rs.getString("grade"));
        r.setResultStatus(rs.getString("result_status"));
        r.setPublished(rs.getBoolean("published"));
        return r;
    }

    private String nvl(String s) { return s == null ? "-" : s; }
}
