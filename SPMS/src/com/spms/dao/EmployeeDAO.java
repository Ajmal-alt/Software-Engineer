package com.spms.dao;

import com.spms.model.Employee;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection conn;

    public EmployeeDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /** Add a new employee (also creates the user record first) */
    public boolean addEmployee(Employee emp) {
        try {
            conn.setAutoCommit(false);

            // 1. Insert into users
            String userSql = "INSERT INTO users (username, password, email, role) VALUES (?,?,?,'EMPLOYEE')";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, emp.getUsername());
                ps.setString(2, emp.getPassword());
                ps.setString(3, emp.getEmail());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) { conn.rollback(); return false; }
                userId = rs.getInt(1);
                emp.setUserId(userId);
            }

            // 2. Insert into employees
            String empSql = "INSERT INTO employees (user_id, employee_code, first_name, last_name, " +
                "date_of_birth, gender, phone, address, city, state, pincode, " +
                "date_of_joining, designation, department_id, qualification, " +
                "bank_account_no, ifsc_code, pan_card, uan_number, status) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'ACTIVE')";
            try (PreparedStatement ps = conn.prepareStatement(empSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, emp.getEmployeeCode());
                ps.setString(3, emp.getFirstName());
                ps.setString(4, emp.getLastName());
                ps.setString(5, emp.getDateOfBirth());
                ps.setString(6, emp.getGender());
                ps.setString(7, emp.getPhone());
                ps.setString(8, emp.getAddress());
                ps.setString(9, emp.getCity());
                ps.setString(10, emp.getState());
                ps.setString(11, emp.getPincode());
                ps.setString(12, emp.getDateOfJoining());
                ps.setString(13, emp.getDesignation());
                ps.setInt(14, emp.getDepartmentId());
                ps.setString(15, emp.getQualification());
                ps.setString(16, emp.getBankAccountNo());
                ps.setString(17, emp.getIfscCode());
                ps.setString(18, emp.getPanCard());
                ps.setString(19, emp.getUanNumber());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) emp.setEmployeeId(rs.getInt(1));
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] addEmployee: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET first_name=?, last_name=?, phone=?, address=?, " +
                     "city=?, state=?, pincode=?, designation=?, department_id=?, " +
                     "qualification=?, status=? WHERE employee_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getAddress());
            ps.setString(5, emp.getCity());
            ps.setString(6, emp.getState());
            ps.setString(7, emp.getPincode());
            ps.setString(8, emp.getDesignation());
            ps.setInt(9, emp.getDepartmentId());
            ps.setString(10, emp.getQualification());
            ps.setString(11, emp.getEmpStatus());
            ps.setInt(12, emp.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] updateEmployee: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateEmployee(int employeeId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE employees SET status='INACTIVE' WHERE employee_id=?")) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] deactivateEmployee: " + e.getMessage());
            return false;
        }
    }

    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT e.*, u.username, u.email, u.role, d.department_name " +
                     "FROM employees e JOIN users u ON e.user_id=u.user_id " +
                     "LEFT JOIN departments d ON e.department_id=d.department_id " +
                     "WHERE e.employee_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapEmployee(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getEmployeeById: " + e.getMessage());
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.*, u.username, u.email, u.role, d.department_name " +
                     "FROM employees e JOIN users u ON e.user_id=u.user_id " +
                     "LEFT JOIN departments d ON e.department_id=d.department_id " +
                     "ORDER BY e.employee_id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapEmployee(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllEmployees: " + e.getMessage());
        }
        return list;
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt("employee_id"));
        emp.setUserId(rs.getInt("user_id"));
        emp.setEmployeeCode(rs.getString("employee_code"));
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        emp.setDateOfBirth(rs.getString("date_of_birth"));
        emp.setGender(rs.getString("gender"));
        emp.setPhone(rs.getString("phone"));
        emp.setAddress(rs.getString("address"));
        emp.setCity(rs.getString("city"));
        emp.setState(rs.getString("state"));
        emp.setPincode(rs.getString("pincode"));
        emp.setDateOfJoining(rs.getString("date_of_joining"));
        emp.setDesignation(rs.getString("designation"));
        emp.setDepartmentId(rs.getInt("department_id"));
        emp.setDepartmentName(rs.getString("department_name"));
        emp.setQualification(rs.getString("qualification"));
        emp.setBankAccountNo(rs.getString("bank_account_no"));
        emp.setIfscCode(rs.getString("ifsc_code"));
        emp.setPanCard(rs.getString("pan_card"));
        emp.setUanNumber(rs.getString("uan_number"));
        emp.setEmpStatus(rs.getString("status"));
        emp.setUsername(rs.getString("username"));
        emp.setEmail(rs.getString("email"));
        emp.setRole(rs.getString("role"));
        return emp;
    }
}
