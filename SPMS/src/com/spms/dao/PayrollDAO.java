package com.spms.dao;

import com.spms.model.Payroll;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {
    private Connection conn;

    public PayrollDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Process payroll for all active employees for a given month/year */
    public int processMonthlyPayroll(int month, int year, int processedByUserId) {
        int processed = 0;
        String empSql = "SELECT e.employee_id FROM employees e WHERE e.status='ACTIVE'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(empSql)) {
            while (rs.next()) {
                int empId = rs.getInt("employee_id");
                if (processForEmployee(empId, month, year, processedByUserId)) processed++;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] processMonthlyPayroll: " + e.getMessage());
        }
        return processed;
    }

    private boolean processForEmployee(int empId, int month, int year, int processedBy) {
        // Check if already processed
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT payroll_id FROM payroll WHERE employee_id=? AND month=? AND year=? AND status='PROCESSED'")) {
            ps.setInt(1, empId); ps.setInt(2, month); ps.setInt(3, year);
            if (ps.executeQuery().next()) return false; // already done
        } catch (SQLException e) { return false; }

        // Get salary structure
        double basic=0, hra=0, conveyance=0, medical=0, special=0, pf=0, ptax=0, itax=0, insurance=0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM salary_structure WHERE employee_id=? AND effective_from<=? ORDER BY effective_from DESC LIMIT 1")) {
            ps.setInt(1, empId);
            ps.setString(2, year + "-" + String.format("%02d", month) + "-01");
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            basic      = rs.getDouble("basic");
            hra        = rs.getDouble("hra");
            conveyance = rs.getDouble("conveyance");
            medical    = rs.getDouble("medical");
            special    = rs.getDouble("special_allowance");
            pf         = rs.getDouble("pf");
            ptax       = rs.getDouble("professional_tax");
            itax       = rs.getDouble("income_tax");
            insurance  = rs.getDouble("insurance");
        } catch (SQLException e) { return false; }

        // Count present days
        int presentDays = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM attendance WHERE employee_id=? AND MONTH(attendance_date)=? AND YEAR(attendance_date)=? AND status IN ('PRESENT','HALF_DAY')")) {
            ps.setInt(1, empId); ps.setInt(2, month); ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) presentDays = rs.getInt(1);
        } catch (SQLException e) { return false; }

        int workingDays = 26; // standard working days
        double ratio       = (presentDays > 0) ? (double) presentDays / workingDays : 1.0;
        double gross       = (basic + hra + conveyance + medical + special) * Math.min(ratio, 1.0);
        double deductions  = pf + ptax + itax + insurance;
        double net         = gross - deductions;

        String upsert = "INSERT INTO payroll (employee_id, month, year, working_days, present_days, " +
                        "gross_salary, total_deductions, net_salary, payment_date, status, processed_by) " +
                        "VALUES (?,?,?,?,?,?,?,?,CURDATE(),'PROCESSED',?) " +
                        "ON DUPLICATE KEY UPDATE gross_salary=VALUES(gross_salary), " +
                        "total_deductions=VALUES(total_deductions), net_salary=VALUES(net_salary), " +
                        "present_days=VALUES(present_days), status='PROCESSED', processed_by=VALUES(processed_by)";
        try (PreparedStatement ps = conn.prepareStatement(upsert)) {
            ps.setInt(1, empId); ps.setInt(2, month); ps.setInt(3, year);
            ps.setInt(4, workingDays); ps.setInt(5, presentDays);
            ps.setDouble(6, gross); ps.setDouble(7, deductions); ps.setDouble(8, net);
            ps.setInt(9, processedBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] processForEmployee: " + e.getMessage());
            return false;
        }
    }

    public Payroll getPayslip(int employeeId, int month, int year) {
        String sql = "SELECT p.*, CONCAT(e.first_name,' ',e.last_name) AS emp_name " +
                     "FROM payroll p JOIN employees e ON p.employee_id=e.employee_id " +
                     "WHERE p.employee_id=? AND p.month=? AND p.year=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId); ps.setInt(2, month); ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPayroll(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getPayslip: " + e.getMessage());
        }
        return null;
    }

    public List<Payroll> getAllPayrollForMonth(int month, int year) {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(e.first_name,' ',e.last_name) AS emp_name " +
                     "FROM payroll p JOIN employees e ON p.employee_id=e.employee_id " +
                     "WHERE p.month=? AND p.year=? ORDER BY e.first_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month); ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapPayroll(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllPayrollForMonth: " + e.getMessage());
        }
        return list;
    }

    public void printPayslipDetails(int employeeId, int month, int year) {
        // Print salary structure breakdown alongside payroll
        String sql = "SELECT ss.*, CONCAT(e.first_name,' ',e.last_name) AS emp_name, " +
                     "e.employee_code, e.designation, d.department_name, " +
                     "p.gross_salary, p.total_deductions, p.net_salary, p.present_days, p.working_days " +
                     "FROM salary_structure ss " +
                     "JOIN employees e ON ss.employee_id=e.employee_id " +
                     "LEFT JOIN departments d ON e.department_id=d.department_id " +
                     "LEFT JOIN payroll p ON ss.employee_id=p.employee_id AND p.month=? AND p.year=? " +
                     "WHERE ss.employee_id=? ORDER BY ss.effective_from DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month); ps.setInt(2, year); ps.setInt(3, employeeId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { System.out.println("  No payslip found."); return; }
            System.out.println("\n" + "=".repeat(55));
            System.out.printf("  PAYSLIP — %s/%d%n", String.format("%02d", month), year);
            System.out.println("=".repeat(55));
            System.out.printf("  Employee : %s (%s)%n", rs.getString("emp_name"), rs.getString("employee_code"));
            System.out.printf("  Designation: %-20s Dept: %s%n", rs.getString("designation"), rs.getString("department_name"));
            System.out.printf("  Present Days: %d / %d%n", rs.getInt("present_days"), rs.getInt("working_days"));
            System.out.println("-".repeat(55));
            System.out.println("  EARNINGS");
            System.out.printf("    Basic Salary          : %10.2f%n", rs.getDouble("basic"));
            System.out.printf("    HRA                   : %10.2f%n", rs.getDouble("hra"));
            System.out.printf("    Conveyance            : %10.2f%n", rs.getDouble("conveyance"));
            System.out.printf("    Medical               : %10.2f%n", rs.getDouble("medical"));
            System.out.printf("    Special Allowance     : %10.2f%n", rs.getDouble("special_allowance"));
            System.out.printf("  GROSS SALARY            : %10.2f%n", rs.getDouble("gross_salary"));
            System.out.println("-".repeat(55));
            System.out.println("  DEDUCTIONS");
            System.out.printf("    Provident Fund (PF)   : %10.2f%n", rs.getDouble("pf"));
            System.out.printf("    Professional Tax      : %10.2f%n", rs.getDouble("professional_tax"));
            System.out.printf("    Income Tax            : %10.2f%n", rs.getDouble("income_tax"));
            System.out.printf("    Insurance             : %10.2f%n", rs.getDouble("insurance"));
            System.out.printf("  TOTAL DEDUCTIONS        : %10.2f%n", rs.getDouble("total_deductions"));
            System.out.println("=".repeat(55));
            System.out.printf("  NET SALARY              : %10.2f%n", rs.getDouble("net_salary"));
            System.out.println("=".repeat(55));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] printPayslipDetails: " + e.getMessage());
        }
    }

    private Payroll mapPayroll(ResultSet rs) throws SQLException {
        Payroll p = new Payroll();
        p.setPayrollId(rs.getInt("payroll_id"));
        p.setEmployeeId(rs.getInt("employee_id"));
        p.setEmployeeName(rs.getString("emp_name"));
        p.setMonth(rs.getInt("month"));
        p.setYear(rs.getInt("year"));
        p.setWorkingDays(rs.getInt("working_days"));
        p.setPresentDays(rs.getInt("present_days"));
        p.setGrossSalary(rs.getDouble("gross_salary"));
        p.setTotalDeductions(rs.getDouble("total_deductions"));
        p.setNetSalary(rs.getDouble("net_salary"));
        p.setPaymentDate(rs.getString("payment_date"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}
