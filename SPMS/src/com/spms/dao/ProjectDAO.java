package com.spms.dao;

import com.spms.model.Department;
import com.spms.model.Project;
import com.spms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private Connection conn;

    public ProjectDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean createProject(Project p) {
        String sql = "INSERT INTO projects (project_name, description, start_date, end_date, status, budget, manager_id) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getProjectName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getStartDate());
            ps.setString(4, p.getEndDate());
            ps.setString(5, p.getStatus() == null ? "ACTIVE" : p.getStatus());
            ps.setDouble(6, p.getBudget());
            ps.setInt(7, p.getManagerId());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) p.setProjectId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] createProject: " + e.getMessage());
        }
        return false;
    }

    public boolean updateProjectStatus(int projectId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE projects SET status=? WHERE project_id=?")) {
            ps.setString(1, status); ps.setInt(2, projectId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] updateProjectStatus: " + e.getMessage());
            return false;
        }
    }

    public boolean assignEmployee(int projectId, int employeeId, String role, int allocationPercent) {
        String sql = "INSERT INTO project_assignments (project_id, employee_id, role, assigned_date, allocation_percent) " +
                     "VALUES (?,?,?,CURDATE(),?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId); ps.setInt(2, employeeId);
            ps.setString(3, role); ps.setInt(4, allocationPercent);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] assignEmployee: " + e.getMessage());
            return false;
        }
    }

    public boolean releaseEmployee(int projectId, int employeeId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE project_assignments SET released_date=CURDATE() WHERE project_id=? AND employee_id=? AND released_date IS NULL")) {
            ps.setInt(1, projectId); ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] releaseEmployee: " + e.getMessage());
            return false;
        }
    }

    public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(e.first_name,' ',e.last_name) AS manager_name " +
                     "FROM projects p LEFT JOIN employees e ON p.manager_id=e.employee_id ORDER BY p.project_id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapProject(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllProjects: " + e.getMessage());
        }
        return list;
    }

    public List<Project> getProjectsByManager(int managerId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(e.first_name,' ',e.last_name) AS manager_name " +
                     "FROM projects p LEFT JOIN employees e ON p.manager_id=e.employee_id " +
                     "WHERE p.manager_id=? ORDER BY p.project_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProject(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getProjectsByManager: " + e.getMessage());
        }
        return list;
    }

    public List<Project> getProjectsByEmployee(int employeeId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(e.first_name,' ',e.last_name) AS manager_name " +
                     "FROM projects p " +
                     "JOIN project_assignments pa ON p.project_id=pa.project_id " +
                     "LEFT JOIN employees e ON p.manager_id=e.employee_id " +
                     "WHERE pa.employee_id=? AND pa.released_date IS NULL ORDER BY p.project_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProject(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getProjectsByEmployee: " + e.getMessage());
        }
        return list;
    }

    public void printTeamMembers(int projectId) {
        String sql = "SELECT e.employee_code, CONCAT(e.first_name,' ',e.last_name) AS name, " +
                     "e.designation, pa.role, pa.allocation_percent, pa.assigned_date " +
                     "FROM project_assignments pa JOIN employees e ON pa.employee_id=e.employee_id " +
                     "WHERE pa.project_id=? AND pa.released_date IS NULL ORDER BY e.first_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("  %-12s %-22s %-20s %-15s %-6s%n",
                    "Code","Name","Designation","Role","Alloc%");
            System.out.println("  " + "-".repeat(78));
            while (rs.next()) {
                System.out.printf("  %-12s %-22s %-20s %-15s %-6d%n",
                        rs.getString("employee_code"),
                        rs.getString("name"),
                        rs.getString("designation"),
                        rs.getString("role"),
                        rs.getInt("allocation_percent"));
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] printTeamMembers: " + e.getMessage());
        }
    }

    // ─── Department CRUD ───────────────────────────────────────────────────────

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM departments ORDER BY department_id")) {
            while (rs.next()) {
                Department d = new Department();
                d.setDepartmentId(rs.getInt("department_id"));
                d.setDepartmentName(rs.getString("department_name"));
                d.setDepartmentHead(rs.getString("department_head"));
                d.setLocation(rs.getString("location"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] getAllDepartments: " + e.getMessage());
        }
        return list;
    }

    public boolean addDepartment(Department d) {
        String sql = "INSERT INTO departments (department_name, department_head, location) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getDepartmentName());
            ps.setString(2, d.getDepartmentHead());
            ps.setString(3, d.getLocation());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] addDepartment: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDepartment(Department d) {
        String sql = "UPDATE departments SET department_name=?, department_head=?, location=? WHERE department_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getDepartmentName());
            ps.setString(2, d.getDepartmentHead());
            ps.setString(3, d.getLocation());
            ps.setInt(4, d.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] updateDepartment: " + e.getMessage());
            return false;
        }
    }

    private Project mapProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setProjectId(rs.getInt("project_id"));
        p.setProjectName(rs.getString("project_name"));
        p.setDescription(rs.getString("description"));
        p.setStartDate(rs.getString("start_date"));
        p.setEndDate(rs.getString("end_date"));
        p.setStatus(rs.getString("status"));
        p.setBudget(rs.getDouble("budget"));
        p.setManagerId(rs.getInt("manager_id"));
        p.setManagerName(rs.getString("manager_name"));
        return p;
    }
}
