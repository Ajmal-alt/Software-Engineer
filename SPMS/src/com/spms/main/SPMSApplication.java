package com.spms.main;

import com.spms.dao.*;
import com.spms.model.*;
import com.spms.util.ConsoleUtil;
import com.spms.util.DBConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * SPMS – Software Personnel Management System
 * Entry point. Provides a fully interactive console application.
 */
public class SPMSApplication {

    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    // DAOs
    private static UserDAO        userDAO;
    private static EmployeeDAO    employeeDAO;
    private static AttendanceDAO  attendanceDAO;
    private static LeaveDAO       leaveDAO;
    private static PayrollDAO     payrollDAO;
    private static ProjectDAO     projectDAO;
    private static PerformanceDAO performanceDAO;

    // ─── Startup ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        ConsoleUtil.printHeader("SOFTWARE PERSONNEL MANAGEMENT SYSTEM");
        System.out.println("  Connecting to database...");
        try {
            userDAO        = new UserDAO();
            employeeDAO    = new EmployeeDAO();
            attendanceDAO  = new AttendanceDAO();
            leaveDAO       = new LeaveDAO();
            payrollDAO     = new PayrollDAO();
            projectDAO     = new ProjectDAO();
            performanceDAO = new PerformanceDAO();
            ConsoleUtil.printSuccess("Database connected successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Cannot connect to database: " + e.getMessage());
            System.out.println("  Please check config/db.properties and ensure MySQL is running.");
            System.exit(1);
        }

        while (true) {
            try {
                if (currentUser == null) showLoginMenu();
                else showRoleDashboard();
            } catch (Exception e) {
                ConsoleUtil.printError("Unexpected error: " + e.getMessage());
            }
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    private static void showLoginMenu() {
        ConsoleUtil.printHeader("LOGIN");
        System.out.print("  Username : ");
        String username = sc.nextLine().trim();
        System.out.print("  Password : ");
        String password = sc.nextLine().trim();

        currentUser = userDAO.authenticate(username, password);
        if (currentUser == null) {
            ConsoleUtil.printError("Invalid credentials or account inactive. Try again.");
        } else {
            ConsoleUtil.printSuccess("Login successful! Role: " + currentUser.getRole());
            userDAO.logAction(currentUser.getUserId(), "LOGIN", "User logged in");
        }
    }

    // ─── Role dispatcher ──────────────────────────────────────────────────────

    private static void showRoleDashboard() {
        switch (currentUser.getRole()) {
            case "EMPLOYEE":        employeeMenu();       break;
            case "PROJECT_MANAGER": projectManagerMenu(); break;
            case "HR":              hrMenu();             break;
            case "ADMIN":           adminMenu();          break;
            default:
                ConsoleUtil.printError("Unknown role.");
                currentUser = null;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EMPLOYEE MENU
    // ══════════════════════════════════════════════════════════════════════════

    private static void employeeMenu() {
        Employee emp = (Employee) currentUser;
        ConsoleUtil.printHeader("EMPLOYEE DASHBOARD");
        emp.displayDashboard();
        System.out.print("\n  Choose option: ");
        int choice = readInt();
        switch (choice) {
            case 1: viewMyProfile(emp);            break;
            case 2: markAttendance(emp);           break;
            case 3: applyLeave(emp);               break;
            case 4: viewLeaveBalance(emp);         break;
            case 5: viewMyPayslip(emp);            break;
            case 6: viewMyProjects(emp);           break;
            case 7: viewMyPerformance(emp);        break;
            case 8: logout();                      break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    private static void viewMyProfile(Employee emp) {
        ConsoleUtil.printSection("MY PROFILE");
        Employee full = employeeDAO.getEmployeeById(emp.getEmployeeId());
        if (full == null) { ConsoleUtil.printError("Profile not found."); return; }
        System.out.printf("  %-22s: %s%n", "Employee Code",   full.getEmployeeCode());
        System.out.printf("  %-22s: %s%n", "Full Name",       full.getFullName());
        System.out.printf("  %-22s: %s%n", "Date of Birth",   full.getDateOfBirth());
        System.out.printf("  %-22s: %s%n", "Gender",          full.getGender());
        System.out.printf("  %-22s: %s%n", "Phone",           full.getPhone());
        System.out.printf("  %-22s: %s%n", "Email",           full.getEmail());
        System.out.printf("  %-22s: %s%n", "Address",         full.getAddress());
        System.out.printf("  %-22s: %s, %s - %s%n", "City/State/Pin",
                full.getCity(), full.getState(), full.getPincode());
        System.out.printf("  %-22s: %s%n", "Date of Joining", full.getDateOfJoining());
        System.out.printf("  %-22s: %s%n", "Designation",     full.getDesignation());
        System.out.printf("  %-22s: %s%n", "Department",      full.getDepartmentName());
        System.out.printf("  %-22s: %s%n", "Qualification",   full.getQualification());
        System.out.printf("  %-22s: %s%n", "Status",          full.getEmpStatus());
    }

    private static void markAttendance(Employee emp) {
        ConsoleUtil.printSection("ATTENDANCE");
        Attendance today = attendanceDAO.getTodayAttendance(emp.getEmployeeId());
        if (today == null) {
            System.out.println("  No check-in recorded today.");
            System.out.println("  1. Check-In   2. Back");
            System.out.print("  Choice: ");
            if (readInt() == 1) {
                if (attendanceDAO.checkIn(emp.getEmployeeId()))
                    ConsoleUtil.printSuccess("Checked in at " + now());
                else ConsoleUtil.printError("Check-in failed.");
            }
        } else if (today.getCheckOutTime() == null) {
            System.out.printf("  Checked in at: %s%n", today.getCheckInTime());
            System.out.println("  1. Check-Out   2. Back");
            System.out.print("  Choice: ");
            if (readInt() == 1) {
                if (attendanceDAO.checkOut(emp.getEmployeeId()))
                    ConsoleUtil.printSuccess("Checked out at " + now());
                else ConsoleUtil.printError("Check-out failed.");
            }
        } else {
            System.out.printf("  Already completed today: In=%s  Out=%s  Hours=%.2f%n",
                    today.getCheckInTime(), today.getCheckOutTime(), today.getTotalHours());
        }
    }

    private static void applyLeave(Employee emp) {
        ConsoleUtil.printSection("APPLY FOR LEAVE");
        List<String[]> types = leaveDAO.getLeaveTypes();
        System.out.println("  Leave Types:");
        for (String[] t : types)
            System.out.printf("    %s. %s%n", t[0], t[1]);
        System.out.print("  Select leave type ID: ");
        int typeId = readInt();
        System.out.print("  Start date (YYYY-MM-DD): ");
        String start = sc.nextLine().trim();
        System.out.print("  End date   (YYYY-MM-DD): ");
        String end = sc.nextLine().trim();
        System.out.print("  Reason: ");
        String reason = sc.nextLine().trim();

        int days = calculateDays(start, end);
        if (days <= 0) { ConsoleUtil.printError("Invalid dates."); return; }

        LeaveApplication la = new LeaveApplication(0, emp.getEmployeeId(), typeId, start, end, reason);
        la.setTotalDays(days);

        if (leaveDAO.applyLeave(la)) {
            ConsoleUtil.printSuccess("Leave applied successfully! Leave ID: " + la.getLeaveId());
            userDAO.logAction(currentUser.getUserId(), "APPLY_LEAVE", "Days=" + days + " Type=" + typeId);
        } else {
            ConsoleUtil.printError("Failed to apply leave. Check your leave balance.");
        }
    }

    private static void viewLeaveBalance(Employee emp) {
        ConsoleUtil.printSection("LEAVE BALANCE");
        List<LeaveBalance> balances = leaveDAO.getAllBalances(emp.getEmployeeId());
        System.out.printf("  %-20s %-8s %-8s %-8s%n", "Leave Type", "Total", "Used", "Remaining");
        System.out.println("  " + "-".repeat(46));
        for (LeaveBalance b : balances) {
            System.out.printf("  %-20s %-8d %-8d %-8d%n",
                    b.getLeaveTypeName(), b.getTotalLeaves(), b.getUsedLeaves(), b.getRemainingLeaves());
        }
        if (balances.isEmpty()) System.out.println("  No balance records found.");
    }

    private static void viewMyPayslip(Employee emp) {
        ConsoleUtil.printSection("VIEW PAYSLIP");
        System.out.print("  Month (1-12): ");
        int month = readInt();
        System.out.print("  Year (e.g. 2026): ");
        int year = readInt();
        payrollDAO.printPayslipDetails(emp.getEmployeeId(), month, year);
    }

    private static void viewMyProjects(Employee emp) {
        ConsoleUtil.printSection("MY PROJECTS");
        List<Project> projects = projectDAO.getProjectsByEmployee(emp.getEmployeeId());
        if (projects.isEmpty()) { System.out.println("  No active project assignments."); return; }
        System.out.printf("  %-5s %-30s %-12s %-12s %-10s%n", "ID", "Project Name", "Start", "End", "Status");
        System.out.println("  " + "-".repeat(72));
        for (Project p : projects) {
            System.out.printf("  %-5d %-30s %-12s %-12s %-10s%n",
                    p.getProjectId(), p.getProjectName(), p.getStartDate(), p.getEndDate(), p.getStatus());
        }
    }

    private static void viewMyPerformance(Employee emp) {
        ConsoleUtil.printSection("MY PERFORMANCE REVIEWS");
        List<PerformanceReview> reviews = performanceDAO.getReviewsByEmployee(emp.getEmployeeId());
        if (reviews.isEmpty()) { System.out.println("  No performance reviews yet."); return; }
        for (PerformanceReview r : reviews) {
            System.out.println("  " + "-".repeat(50));
            System.out.printf("  Period   : %s   Date: %s%n", r.getReviewPeriod(), r.getReviewDate());
            System.out.printf("  Reviewer : %s%n", r.getReviewerName());
            System.out.printf("  Rating   : %.1f / 5.0%n", r.getRating());
            System.out.printf("  Comments : %s%n", r.getComments());
            System.out.printf("  Status   : %s%n", r.getStatus());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PROJECT MANAGER MENU
    // ══════════════════════════════════════════════════════════════════════════

    private static void projectManagerMenu() {
        Employee pm = (Employee) currentUser;
        ConsoleUtil.printHeader("PROJECT MANAGER DASHBOARD");
        pm.displayDashboard();
        System.out.print("\n  Choose option: ");
        int choice = readInt();
        switch (choice) {
            case 1: viewMyProfile(pm);                break;
            case 2: markAttendance(pm);               break;
            case 3: applyLeave(pm);                   break;
            case 4: viewLeaveBalance(pm);             break;
            case 5: viewMyPayslip(pm);                break;
            case 6: viewTeamAttendance(pm);           break;
            case 7: managePendingLeaves(pm);          break;
            case 8: manageProjects(pm);               break;
            case 9: evaluatePerformance(pm);          break;
            case 10: logout();                        break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    private static void viewTeamAttendance(Employee pm) {
        ConsoleUtil.printSection("TEAM ATTENDANCE – TODAY");
        List<Attendance> list = attendanceDAO.getTeamAttendanceToday(pm.getEmployeeId());
        if (list.isEmpty()) { System.out.println("  No attendance records for your team today."); return; }
        System.out.printf("  %-22s %-10s %-10s %-6s%n", "Employee", "Check-In", "Check-Out", "Hours");
        System.out.println("  " + "-".repeat(52));
        for (Attendance a : list) {
            System.out.printf("  %-22s %-10s %-10s %-6.2f%n",
                    a.getEmployeeName(), nvl(a.getCheckInTime()), nvl(a.getCheckOutTime()), a.getTotalHours());
        }
    }

    private static void managePendingLeaves(Employee pm) {
        ConsoleUtil.printSection("PENDING LEAVE REQUESTS");
        List<LeaveApplication> pending = leaveDAO.getPendingLeaves();
        if (pending.isEmpty()) { System.out.println("  No pending leave requests."); return; }
        System.out.printf("  %-5s %-22s %-15s %-12s %-12s %-5s%n",
                "ID","Employee","Leave Type","From","To","Days");
        System.out.println("  " + "-".repeat(74));
        for (LeaveApplication la : pending) {
            System.out.printf("  %-5d %-22s %-15s %-12s %-12s %-5d%n",
                    la.getLeaveId(), la.getEmployeeName(), la.getLeaveTypeName(),
                    la.getStartDate(), la.getEndDate(), la.getTotalDays());
        }
        System.out.print("\n  Enter Leave ID to act on (0=back): ");
        int leaveId = readInt();
        if (leaveId == 0) return;
        System.out.println("  1. Approve   2. Reject");
        System.out.print("  Choice: ");
        int action = readInt();
        if (action == 1) {
            if (leaveDAO.approveLeave(leaveId, pm.getEmployeeId()))
                ConsoleUtil.printSuccess("Leave approved.");
            else ConsoleUtil.printError("Approval failed.");
        } else if (action == 2) {
            System.out.print("  Reason for rejection: ");
            String reason = sc.nextLine().trim();
            if (leaveDAO.rejectLeave(leaveId, pm.getEmployeeId(), reason))
                ConsoleUtil.printSuccess("Leave rejected.");
            else ConsoleUtil.printError("Rejection failed.");
        }
    }

    private static void manageProjects(Employee pm) {
        ConsoleUtil.printSection("MY PROJECTS");
        List<Project> projects = projectDAO.getProjectsByManager(pm.getEmployeeId());
        System.out.printf("  %-5s %-30s %-10s%n", "ID", "Project Name", "Status");
        System.out.println("  " + "-".repeat(48));
        for (Project p : projects) {
            System.out.printf("  %-5d %-30s %-10s%n", p.getProjectId(), p.getProjectName(), p.getStatus());
        }
        System.out.println("\n  Options:");
        System.out.println("    1. Create New Project");
        System.out.println("    2. Assign Employee to Project");
        System.out.println("    3. View Team Members");
        System.out.println("    4. Update Project Status");
        System.out.println("    5. Back");
        System.out.print("  Choice: ");
        int choice = readInt();
        switch (choice) {
            case 1: createProject(pm);              break;
            case 2: assignEmployeeToProject(pm);    break;
            case 3: viewTeamMembers();              break;
            case 4: updateProjectStatus();          break;
        }
    }

    private static void createProject(Employee pm) {
        ConsoleUtil.printSection("CREATE PROJECT");
        Project p = new Project();
        System.out.print("  Project Name: "); p.setProjectName(sc.nextLine().trim());
        System.out.print("  Description : "); p.setDescription(sc.nextLine().trim());
        System.out.print("  Start Date (YYYY-MM-DD): "); p.setStartDate(sc.nextLine().trim());
        System.out.print("  End Date   (YYYY-MM-DD): "); p.setEndDate(sc.nextLine().trim());
        System.out.print("  Budget (e.g. 500000): "); p.setBudget(readDouble());
        p.setManagerId(pm.getEmployeeId());
        p.setStatus("ACTIVE");
        if (projectDAO.createProject(p)) ConsoleUtil.printSuccess("Project created! ID: " + p.getProjectId());
        else ConsoleUtil.printError("Failed to create project.");
    }

    private static void assignEmployeeToProject(Employee pm) {
        ConsoleUtil.printSection("ASSIGN EMPLOYEE");
        System.out.print("  Project ID: ");   int projectId = readInt();
        System.out.print("  Employee ID: ");  int empId     = readInt();
        System.out.print("  Role on project: "); String role = sc.nextLine().trim();
        System.out.print("  Allocation % (e.g. 80): "); int alloc = readInt();
        if (projectDAO.assignEmployee(projectId, empId, role, alloc))
            ConsoleUtil.printSuccess("Employee assigned.");
        else ConsoleUtil.printError("Assignment failed.");
    }

    private static void viewTeamMembers() {
        System.out.print("  Project ID: "); int pid = readInt();
        ConsoleUtil.printSection("TEAM MEMBERS – Project " + pid);
        projectDAO.printTeamMembers(pid);
    }

    private static void updateProjectStatus() {
        System.out.print("  Project ID: "); int pid = readInt();
        System.out.println("  Status options: ACTIVE / COMPLETED / ON_HOLD");
        System.out.print("  New status: "); String status = sc.nextLine().trim().toUpperCase();
        if (projectDAO.updateProjectStatus(pid, status)) ConsoleUtil.printSuccess("Status updated.");
        else ConsoleUtil.printError("Update failed.");
    }

    private static void evaluatePerformance(Employee pm) {
        ConsoleUtil.printSection("EVALUATE PERFORMANCE");
        System.out.print("  Employee ID to review: "); int empId = readInt();
        System.out.print("  Review Period (e.g. Q1 2026): "); String period = sc.nextLine().trim();
        System.out.print("  Rating (1.0 – 5.0): "); double rating = readDouble();
        System.out.print("  Comments: "); String comments = sc.nextLine().trim();

        PerformanceReview r = new PerformanceReview();
        r.setEmployeeId(empId);
        r.setReviewerId(pm.getEmployeeId());
        r.setReviewPeriod(period);
        r.setReviewDate(today());
        r.setRating(rating);
        r.setComments(comments);

        if (performanceDAO.addReview(r)) ConsoleUtil.printSuccess("Review submitted. ID: " + r.getReviewId());
        else ConsoleUtil.printError("Failed to submit review.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HR MENU
    // ══════════════════════════════════════════════════════════════════════════

    private static void hrMenu() {
        ConsoleUtil.printHeader("HR MANAGER DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        int choice = readInt();
        switch (choice) {
            case 1: addNewEmployee();        break;
            case 2: viewAllEmployees();      break;
            case 3: deactivateEmployee();    break;
            case 4: processPayroll();        break;
            case 5: generateReports();       break;
            case 6: manageDepartments();     break;
            case 7: logout();               break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    private static void addNewEmployee() {
        ConsoleUtil.printSection("ADD NEW EMPLOYEE");
        Employee emp = new Employee();
        System.out.print("  Username       : "); emp.setUsername(sc.nextLine().trim());
        System.out.print("  Password       : "); emp.setPassword(sc.nextLine().trim());
        System.out.print("  Email          : "); emp.setEmail(sc.nextLine().trim());
        System.out.print("  Employee Code  : "); emp.setEmployeeCode(sc.nextLine().trim());
        System.out.print("  First Name     : "); emp.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name      : "); emp.setLastName(sc.nextLine().trim());
        System.out.print("  Date of Birth (YYYY-MM-DD): "); emp.setDateOfBirth(sc.nextLine().trim());
        System.out.print("  Gender (MALE/FEMALE/OTHER) : "); emp.setGender(sc.nextLine().trim().toUpperCase());
        System.out.print("  Phone          : "); emp.setPhone(sc.nextLine().trim());
        System.out.print("  Address        : "); emp.setAddress(sc.nextLine().trim());
        System.out.print("  City           : "); emp.setCity(sc.nextLine().trim());
        System.out.print("  State          : "); emp.setState(sc.nextLine().trim());
        System.out.print("  Pincode        : "); emp.setPincode(sc.nextLine().trim());
        System.out.print("  Date of Joining (YYYY-MM-DD): "); emp.setDateOfJoining(sc.nextLine().trim());
        System.out.print("  Designation    : "); emp.setDesignation(sc.nextLine().trim());

        // Show departments
        List<Department> depts = projectDAO.getAllDepartments();
        System.out.println("  Departments:");
        for (Department d : depts)
            System.out.printf("    %d. %s%n", d.getDepartmentId(), d.getDepartmentName());
        System.out.print("  Department ID  : "); emp.setDepartmentId(readInt());

        System.out.print("  Qualification  : "); emp.setQualification(sc.nextLine().trim());
        System.out.print("  Bank Account No: "); emp.setBankAccountNo(sc.nextLine().trim());
        System.out.print("  IFSC Code      : "); emp.setIfscCode(sc.nextLine().trim());
        System.out.print("  PAN Card       : "); emp.setPanCard(sc.nextLine().trim());
        System.out.print("  UAN Number     : "); emp.setUanNumber(sc.nextLine().trim());
        emp.setRole("EMPLOYEE");
        emp.setEmpStatus("ACTIVE");

        if (employeeDAO.addEmployee(emp)) {
            ConsoleUtil.printSuccess("Employee added! ID: " + emp.getEmployeeId());
            userDAO.logAction(currentUser.getUserId(), "ADD_EMPLOYEE", "EmpCode=" + emp.getEmployeeCode());
        } else {
            ConsoleUtil.printError("Failed to add employee.");
        }
    }

    private static void viewAllEmployees() {
        ConsoleUtil.printSection("ALL EMPLOYEES");
        List<Employee> list = employeeDAO.getAllEmployees();
        if (list.isEmpty()) { System.out.println("  No employees found."); return; }
        System.out.printf("  %-5s %-12s %-24s %-20s %-15s %-10s%n",
                "ID","Code","Name","Designation","Department","Status");
        System.out.println("  " + "-".repeat(90));
        for (Employee e : list) {
            System.out.printf("  %-5d %-12s %-24s %-20s %-15s %-10s%n",
                    e.getEmployeeId(), e.getEmployeeCode(), e.getFullName(),
                    e.getDesignation(), nvl(e.getDepartmentName()), e.getEmpStatus());
        }
    }

    private static void deactivateEmployee() {
        ConsoleUtil.printSection("DEACTIVATE EMPLOYEE");
        viewAllEmployees();
        System.out.print("\n  Enter Employee ID to deactivate (0=cancel): ");
        int id = readInt();
        if (id == 0) return;
        if (employeeDAO.deactivateEmployee(id)) {
            ConsoleUtil.printSuccess("Employee deactivated.");
            userDAO.logAction(currentUser.getUserId(), "DEACTIVATE_EMPLOYEE", "EmpID=" + id);
        } else ConsoleUtil.printError("Deactivation failed.");
    }

    private static void processPayroll() {
        ConsoleUtil.printSection("PROCESS PAYROLL");
        System.out.print("  Month (1-12): "); int month = readInt();
        System.out.print("  Year        : "); int year  = readInt();
        System.out.println("  Processing... please wait.");
        int count = payrollDAO.processMonthlyPayroll(month, year, currentUser.getUserId());
        ConsoleUtil.printSuccess("Payroll processed for " + count + " employee(s).");

        // Show summary
        List<Payroll> payrolls = payrollDAO.getAllPayrollForMonth(month, year);
        System.out.printf("%n  %-5s %-22s %-12s %-14s %-12s%n","ID","Employee","Gross","Deductions","Net Salary");
        System.out.println("  " + "-".repeat(68));
        double totalNet = 0;
        for (Payroll p : payrolls) {
            System.out.printf("  %-5d %-22s %-12.2f %-14.2f %-12.2f%n",
                    p.getPayrollId(), p.getEmployeeName(),
                    p.getGrossSalary(), p.getTotalDeductions(), p.getNetSalary());
            totalNet += p.getNetSalary();
        }
        System.out.println("  " + "-".repeat(68));
        System.out.printf("  %-28s %s %.2f%n", "TOTAL PAYROLL DISBURSEMENT", "₹", totalNet);
        userDAO.logAction(currentUser.getUserId(), "PROCESS_PAYROLL", "Month=" + month + "/" + year + " Count=" + count);
    }

    private static void generateReports() {
        ConsoleUtil.printSection("REPORTS");
        System.out.println("  1. Employee Directory");
        System.out.println("  2. Project-wise Allocation");
        System.out.println("  3. All Performance Reviews");
        System.out.println("  4. Payroll Summary");
        System.out.println("  5. Back");
        System.out.print("  Choose report: ");
        int choice = readInt();
        switch (choice) {
            case 1: viewAllEmployees(); break;
            case 2: projectAllocationReport(); break;
            case 3: allPerformanceReviews(); break;
            case 4: payrollSummaryReport(); break;
        }
    }

    private static void projectAllocationReport() {
        ConsoleUtil.printSection("PROJECT-WISE ALLOCATION");
        List<Project> projects = projectDAO.getAllProjects();
        for (Project p : projects) {
            System.out.printf("%n  [%d] %s (%s) – Manager: %s%n",
                    p.getProjectId(), p.getProjectName(), p.getStatus(), nvl(p.getManagerName()));
            projectDAO.printTeamMembers(p.getProjectId());
        }
    }

    private static void allPerformanceReviews() {
        ConsoleUtil.printSection("ALL PERFORMANCE REVIEWS");
        List<PerformanceReview> reviews = performanceDAO.getAllReviews();
        if (reviews.isEmpty()) { System.out.println("  No reviews found."); return; }
        System.out.printf("  %-5s %-22s %-22s %-12s %-6s %-10s%n",
                "ID","Employee","Reviewer","Period","Rating","Status");
        System.out.println("  " + "-".repeat(80));
        for (PerformanceReview r : reviews) {
            System.out.printf("  %-5d %-22s %-22s %-12s %-6.1f %-10s%n",
                    r.getReviewId(), r.getEmployeeName(), r.getReviewerName(),
                    r.getReviewPeriod(), r.getRating(), r.getStatus());
        }
    }

    private static void payrollSummaryReport() {
        System.out.print("  Month (1-12): "); int month = readInt();
        System.out.print("  Year        : "); int year  = readInt();
        ConsoleUtil.printSection("PAYROLL SUMMARY – " + String.format("%02d/%d", month, year));
        List<Payroll> list = payrollDAO.getAllPayrollForMonth(month, year);
        if (list.isEmpty()) { System.out.println("  No payroll processed for this period."); return; }
        System.out.printf("  %-22s %-8s %-8s %-12s %-12s %-12s%n",
                "Employee","Work","Present","Gross","Deductions","Net");
        System.out.println("  " + "-".repeat(76));
        for (Payroll p : list) {
            System.out.printf("  %-22s %-8d %-8d %-12.2f %-12.2f %-12.2f%n",
                    p.getEmployeeName(), p.getWorkingDays(), p.getPresentDays(),
                    p.getGrossSalary(), p.getTotalDeductions(), p.getNetSalary());
        }
    }

    private static void manageDepartments() {
        ConsoleUtil.printSection("MANAGE DEPARTMENTS");
        List<Department> depts = projectDAO.getAllDepartments();
        System.out.printf("  %-5s %-25s %-20s %-15s%n","ID","Name","Head","Location");
        System.out.println("  " + "-".repeat(67));
        for (Department d : depts)
            System.out.printf("  %-5d %-25s %-20s %-15s%n",
                    d.getDepartmentId(), d.getDepartmentName(), nvl(d.getDepartmentHead()), nvl(d.getLocation()));
        System.out.println("\n  1. Add Department   2. Update Department   3. Back");
        System.out.print("  Choice: ");
        int choice = readInt();
        if (choice == 1) {
            Department d = new Department();
            System.out.print("  Name: "); d.setDepartmentName(sc.nextLine().trim());
            System.out.print("  Head: "); d.setDepartmentHead(sc.nextLine().trim());
            System.out.print("  Location: "); d.setLocation(sc.nextLine().trim());
            if (projectDAO.addDepartment(d)) ConsoleUtil.printSuccess("Department added.");
            else ConsoleUtil.printError("Failed.");
        } else if (choice == 2) {
            System.out.print("  Department ID to update: "); int id = readInt();
            Department d = new Department();
            d.setDepartmentId(id);
            System.out.print("  New Name: ");     d.setDepartmentName(sc.nextLine().trim());
            System.out.print("  New Head: ");     d.setDepartmentHead(sc.nextLine().trim());
            System.out.print("  New Location: "); d.setLocation(sc.nextLine().trim());
            if (projectDAO.updateDepartment(d)) ConsoleUtil.printSuccess("Updated.");
            else ConsoleUtil.printError("Failed.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ══════════════════════════════════════════════════════════════════════════

    private static void adminMenu() {
        ConsoleUtil.printHeader("ADMIN DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        int choice = readInt();
        switch (choice) {
            case 1: listAllUsers();          break;
            case 2: changeUserRole();        break;
            case 3: viewSystemLogs();        break;
            case 4: configureSystem();       break;
            case 5: manageDepartments();     break;
            case 6: logout();               break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    private static void listAllUsers() {
        ConsoleUtil.printSection("ALL USERS");
        List<User> users = userDAO.getAllUsers();
        System.out.printf("  %-5s %-20s %-30s %-18s %-8s%n","ID","Username","Email","Role","Active");
        System.out.println("  " + "-".repeat(83));
        for (User u : users) {
            System.out.printf("  %-5d %-20s %-30s %-18s %-8s%n",
                    u.getUserId(), u.getUsername(), u.getEmail(), u.getRole(), u.isStatus() ? "Yes" : "No");
        }
    }

    private static void changeUserRole() {
        ConsoleUtil.printSection("CHANGE USER ROLE");
        listAllUsers();
        System.out.print("\n  User ID to deactivate (0=cancel): ");
        int id = readInt();
        if (id == 0) return;
        if (userDAO.deactivateUser(id)) ConsoleUtil.printSuccess("User deactivated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void viewSystemLogs() {
        ConsoleUtil.printSection("SYSTEM LOGS (last 30)");
        userDAO.printSystemLogs();
    }

    private static void configureSystem() {
        ConsoleUtil.printSection("SYSTEM CONFIGURATION");
        System.out.println("  Current database config is loaded from config/db.properties");
        System.out.println("  Edit that file to change DB host, username, or password.");
        System.out.println("  Restart the application for changes to take effect.");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void logout() {
        userDAO.logAction(currentUser.getUserId(), "LOGOUT", "User logged out");
        ConsoleUtil.printSuccess("Logged out successfully. Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    private static int readInt() {
        try {
            String line = sc.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double readDouble() {
        try {
            String line = sc.nextLine().trim();
            return Double.parseDouble(line);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String nvl(String s) { return s == null ? "-" : s; }

    private static String now() {
        return new java.util.Date().toString();
    }

    private static String today() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }

    private static int calculateDays(String start, String end) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            long diff = sdf.parse(end).getTime() - sdf.parse(start).getTime();
            return (int)(diff / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            return -1;
        }
    }
}
