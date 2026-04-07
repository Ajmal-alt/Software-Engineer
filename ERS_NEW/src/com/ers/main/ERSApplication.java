package com.ers.main;

import com.ers.dao.*;
import com.ers.model.*;
import com.ers.util.ConsoleUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ERSApplication {

    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    private static UserDAO         userDAO;
    private static StudentDAO      studentDAO;
    private static ExamDAO         examDAO;
    private static RegistrationDAO registrationDAO;
    private static ResultDAO       resultDAO;

    // ─── Startup ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        ConsoleUtil.printHeader("EXAM REGISTRATION SYSTEM (ERS)");
        System.out.println("  Connecting to database...");
        try {
            userDAO         = new UserDAO();
            studentDAO      = new StudentDAO();
            examDAO         = new ExamDAO();
            registrationDAO = new RegistrationDAO();
            resultDAO       = new ResultDAO();
            ConsoleUtil.printSuccess("Connected successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Database connection failed: " + e.getMessage());
            System.out.println("  Check config/db.properties and ensure MySQL is running.");
            System.exit(1);
        }
        while (true) {
            try {
                if (currentUser == null) showLoginMenu();
                else                     showRoleDashboard();
            } catch (Exception e) {
                ConsoleUtil.printError("Unexpected error: " + e.getMessage());
            }
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────────
    private static void showLoginMenu() {
        ConsoleUtil.printHeader("LOGIN");
        System.out.print("  Username : "); String u = sc.nextLine().trim();
        System.out.print("  Password : "); String p = sc.nextLine().trim();
        currentUser = userDAO.authenticate(u, p);
        if (currentUser == null) {
            ConsoleUtil.printError("Invalid credentials. Try again.");
        } else {
            if ("STUDENT".equals(currentUser.getRole())) {
                Student full = studentDAO.getStudentByUserId(currentUser.getUserId());
                if (full != null) {
                    Student s = (Student) currentUser;
                    s.setStudentId(full.getStudentId());
                    s.setStudentCode(full.getStudentCode());
                    s.setFirstName(full.getFirstName());
                    s.setLastName(full.getLastName());
                    s.setDepartment(full.getDepartment());
                    s.setCourse(full.getCourse());
                    s.setSemester(full.getSemester());
                    s.setRollNumber(full.getRollNumber());
                    s.setAddress(full.getAddress());
                    s.setCity(full.getCity());
                    s.setState(full.getState());
                    s.setPincode(full.getPincode());
                    s.setStudentStatus(full.getStudentStatus());
                }
            }
            ConsoleUtil.printSuccess("Welcome " + currentUser.getUsername()
                    + "! Role: " + currentUser.getRole());
            userDAO.logAction(currentUser.getUserId(), "LOGIN", "User logged in");
        }
    }

    private static void showRoleDashboard() {
        switch (currentUser.getRole()) {
            case "ADMIN":    adminMenu();    break;
            case "EXAMINER": examinerMenu(); break;
            case "STUDENT":  studentMenu();  break;
            default: currentUser = null;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void adminMenu() {
        ConsoleUtil.printHeader("ADMIN DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  manageStudents();          break;
            case 2:  manageSubjects();          break;
            case 3:  manageExams();             break;
            case 4:  manageExamSchedule();      break;
            case 5:  viewAllRegistrations();    break;
            case 6:  approveRegistration();     break;
            case 7:  issueHallTicketsMenu();    break;
            case 8:  enterResultsMenu();        break;
            case 9:  publishResultsMenu();      break;
            case 10: viewResultsMenu();         break;
            case 11: reportsMenu();             break;
            case 12: manageUsers();             break;
            case 13: viewSystemLogs();          break;
            case 14: logout();                 break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXAMINER MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void examinerMenu() {
        ConsoleUtil.printHeader("EXAMINER DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  manageSubjects();          break;
            case 2:  manageExams();             break;
            case 3:  manageExamSchedule();      break;
            case 4:  viewAllRegistrations();    break;
            case 5:  approveRegistration();     break;
            case 6:  issueHallTicketsMenu();    break;
            case 7:  enterResultsMenu();        break;
            case 8:  publishResultsMenu();      break;
            case 9:  viewResultsMenu();         break;
            case 10: reportsMenu();             break;
            case 11: logout();                 break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STUDENT MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void studentMenu() {
        Student student = (Student) currentUser;
        ConsoleUtil.printHeader("STUDENT DASHBOARD");
        student.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1: viewMyProfile(student);        break;
            case 2: viewAvailableExams();          break;
            case 3: registerForExam(student);      break;
            case 4: viewMyRegistrations(student);  break;
            case 5: payExamFee(student);           break;
            case 6: downloadHallTicket(student);   break;
            case 7: viewMyResults(student);        break;
            case 8: viewResultSummary(student);    break;
            case 9: logout();                     break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STUDENT SELF-SERVICE
    // ══════════════════════════════════════════════════════════════════════════
    private static void viewMyProfile(Student s) {
        ConsoleUtil.printSection("MY PROFILE");
        Student full = studentDAO.getStudentById(s.getStudentId());
        if (full == null) { ConsoleUtil.printError("Profile not found."); return; }
        System.out.printf("  %-24s: %s%n", "Student Code",    full.getStudentCode());
        System.out.printf("  %-24s: %s%n", "Full Name",       full.getFullName());
        System.out.printf("  %-24s: %s%n", "Roll Number",     nvl(full.getRollNumber()));
        System.out.printf("  %-24s: %s%n", "Department",      nvl(full.getDepartment()));
        System.out.printf("  %-24s: %s%n", "Course",          nvl(full.getCourse()));
        System.out.printf("  %-24s: %d%n",  "Semester",       full.getSemester());
        System.out.printf("  %-24s: %d%n",  "Year of Adm.",   full.getYearOfAdmission());
        System.out.printf("  %-24s: %s%n", "Phone",           nvl(full.getPhone()));
        System.out.printf("  %-24s: %s%n", "Email",           full.getEmail());
        System.out.printf("  %-24s: %s, %s%n", "City/State",  nvl(full.getCity()), nvl(full.getState()));
        System.out.printf("  %-24s: %s%n", "Status",          full.getStudentStatus());
    }

    private static void viewAvailableExams() {
        ConsoleUtil.printSection("AVAILABLE EXAMS (Registration Open)");
        List<Exam> list = examDAO.getOpenExams();
        if (list.isEmpty()) {
            System.out.println("  No exams currently open for registration.");
            return;
        }
        printExamTable(list);
    }

    private static void registerForExam(Student student) {
        ConsoleUtil.printSection("REGISTER FOR EXAM");
        viewAvailableExams();
        System.out.print("\n  Exam ID to register: "); int examId = readInt();
        Exam exam = examDAO.getExamById(examId);
        if (exam == null || !"REGISTRATION_OPEN".equals(exam.getStatus())) {
            ConsoleUtil.printError("Exam not found or not open for registration."); return;
        }
        System.out.println("\n  Exam         : " + exam.getExamName());
        System.out.printf("  Fee/Subject  : Rs. %.2f%n", exam.getFeePerSubject());
        System.out.println("  Max Subjects : " + exam.getMaxSubjects());

        List<ExamSubject> schedule = examDAO.getExamSchedule(examId);
        if (schedule.isEmpty()) {
            ConsoleUtil.printError("No subjects added to this exam schedule yet."); return;
        }
        System.out.println("\n  Available Subjects:");
        System.out.printf("  %-5s %-8s %-32s %-12s %-8s %-12s%n",
                "ID", "Code", "Subject", "Date", "Time", "Venue");
        System.out.println("  " + "-".repeat(79));
        for (ExamSubject es : schedule)
            System.out.printf("  %-5d %-8s %-32s %-12s %-8s %-12s%n",
                    es.getId(), es.getSubjectCode(), es.getSubjectName(),
                    nvl(es.getExamDate()), nvl(es.getExamTime()), nvl(es.getVenue()));

        System.out.println("\n  Enter exam_subject IDs (comma-separated, e.g. 1,2,3):");
        System.out.print("  IDs: "); String input = sc.nextLine().trim();
        String[] parts = input.split(",");
        List<ExamSubject> selected = new ArrayList<ExamSubject>();
        for (String part : parts) {
            try {
                int esId = Integer.parseInt(part.trim());
                ExamSubject es = examDAO.getExamSubjectById(esId);
                if (es != null && es.getExamId() == examId) {
                    selected.add(es);
                } else {
                    ConsoleUtil.printError("Invalid subject ID: " + esId); return;
                }
            } catch (NumberFormatException e) {
                ConsoleUtil.printError("Invalid input: " + part); return;
            }
        }
        if (selected.isEmpty()) { ConsoleUtil.printError("No subjects selected."); return; }
        if (selected.size() > exam.getMaxSubjects()) {
            ConsoleUtil.printError("Too many subjects. Maximum: " + exam.getMaxSubjects()); return;
        }

        double totalFee = selected.size() * exam.getFeePerSubject();
        System.out.printf("\n  Selected     : %d subject(s)%n", selected.size());
        System.out.printf("  Total Fee    : Rs. %.2f%n", totalFee);
        System.out.print("  Confirm registration? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) return;

        int regId = registrationDAO.registerForExam(
                student.getStudentId(), examId, selected, exam.getFeePerSubject());
        if (regId == -2) {
            ConsoleUtil.printError("Already registered for this exam.");
        } else if (regId > 0) {
            Registration r = registrationDAO.getRegistrationById(regId);
            ConsoleUtil.printSuccess("Registration successful!");
            System.out.printf("  Reg Number   : %s%n", r.getRegNumber());
            System.out.printf("  Total Fee    : Rs. %.2f%n", r.getTotalFee());
            ConsoleUtil.printInfo("Please pay the fee to confirm your registration.");
            userDAO.logAction(currentUser.getUserId(), "REGISTER_EXAM",
                    "RegID=" + regId + " ExamID=" + examId);
        } else {
            ConsoleUtil.printError("Registration failed. Please try again.");
        }
    }

    private static void viewMyRegistrations(Student student) {
        ConsoleUtil.printSection("MY REGISTRATIONS");
        List<Registration> list = registrationDAO.getRegistrationsByStudent(student.getStudentId());
        if (list.isEmpty()) { System.out.println("  No registrations found."); return; }
        printRegistrationTable(list);
    }

    private static void payExamFee(Student student) {
        ConsoleUtil.printSection("PAY EXAM FEE");
        List<Registration> all = registrationDAO.getRegistrationsByStudent(student.getStudentId());
        List<Registration> unpaid = new ArrayList<Registration>();
        for (Registration r : all) {
            if (!r.isFeePaid()) unpaid.add(r);
        }
        if (unpaid.isEmpty()) { System.out.println("  No pending fee payments."); return; }
        printRegistrationTable(unpaid);
        System.out.print("\n  Registration ID to pay: "); int regId = readInt();
        Registration reg = registrationDAO.getRegistrationById(regId);
        if (reg == null || reg.getStudentId() != student.getStudentId()) {
            ConsoleUtil.printError("Invalid registration."); return;
        }
        System.out.printf("  Amount due   : Rs. %.2f%n", reg.getTotalFee());
        System.out.println("  Payment Mode : UPI / NET_BANKING / CARD / DD");
        System.out.print("  Mode         : "); String mode = sc.nextLine().trim();
        System.out.print("  Transaction Ref (Enter to auto-generate): "); String ref = sc.nextLine().trim();
        if (ref.isEmpty()) ref = "PAY-ERS-" + (System.currentTimeMillis() % 100000);
        if (registrationDAO.payFee(regId, ref)) {
            ConsoleUtil.printSuccess("Fee paid! Registration confirmed. Ref: " + ref);
            ConsoleUtil.printInfo("Hall ticket will be issued shortly by admin/examiner.");
            userDAO.logAction(currentUser.getUserId(), "PAY_FEE",
                    "RegID=" + regId + " Ref=" + ref);
        } else {
            ConsoleUtil.printError("Payment failed. Please try again.");
        }
    }

    private static void downloadHallTicket(Student student) {
        ConsoleUtil.printSection("HALL TICKET");
        List<Registration> all = registrationDAO.getRegistrationsByStudent(student.getStudentId());
        List<Registration> withTicket = new ArrayList<Registration>();
        for (Registration r : all) {
            if (r.isHallTicketIssued()) withTicket.add(r);
        }
        if (withTicket.isEmpty()) {
            System.out.println("  No hall tickets issued yet.");
            ConsoleUtil.printInfo("Hall tickets are issued after fee payment is verified.");
            return;
        }
        printRegistrationTable(withTicket);
        System.out.print("\n  Registration ID to print: "); int regId = readInt();
        registrationDAO.printHallTicket(regId, examDAO, studentDAO);
    }

    private static void viewMyResults(Student student) {
        ConsoleUtil.printSection("MY RESULTS");
        List<Result> list = resultDAO.getResultsByStudent(student.getStudentId());
        if (list.isEmpty()) {
            System.out.println("  No published results found yet."); return;
        }
        String currentExam = "";
        for (Result r : list) {
            if (!r.getExamName().equals(currentExam)) {
                currentExam = r.getExamName();
                System.out.println("\n  [" + currentExam + "]");
                System.out.printf("  %-8s %-30s %-6s %-6s %-6s %-8s%n",
                        "Code", "Subject", "Max", "Got", "Grade", "Status");
                System.out.println("  " + "-".repeat(66));
            }
            String subj = r.getSubjectName().length() > 28
                    ? r.getSubjectName().substring(0, 27) + "." : r.getSubjectName();
            System.out.printf("  %-8s %-30s %-6d %-6.1f %-6s %-8s%n",
                    r.getSubjectCode(), subj, r.getMaxMarks(),
                    r.getMarksObtained(), r.getGrade(), r.getResultStatus());
        }
    }

    private static void viewResultSummary(Student student) {
        ConsoleUtil.printSection("RESULT SUMMARY / MARKSHEET");
        List<Registration> regs = registrationDAO.getRegistrationsByStudent(student.getStudentId());
        if (regs.isEmpty()) { System.out.println("  No registrations found."); return; }
        printRegistrationTable(regs);
        System.out.print("\n  Registration ID for marksheet: "); int regId = readInt();
        resultDAO.printMarksheet(regId);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STUDENT MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageStudents() {
        ConsoleUtil.printSection("MANAGE STUDENTS");
        System.out.println("  1. Add New Student");
        System.out.println("  2. View All Students");
        System.out.println("  3. Update Student Status");
        System.out.println("  4. Update Semester");
        System.out.println("  5. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addStudent();          break;
            case 2: viewAllStudents();     break;
            case 3: updateStudentStatus(); break;
            case 4: updateSemester();      break;
        }
    }

    private static void addStudent() {
        ConsoleUtil.printSection("ADD NEW STUDENT");
        Student s = new Student();
        System.out.print("  Username                       : "); s.setUsername(sc.nextLine().trim());
        System.out.print("  Password                       : "); s.setPassword(sc.nextLine().trim());
        System.out.print("  Email                          : "); s.setEmail(sc.nextLine().trim());
        System.out.print("  Student Code (e.g. STD-005)    : "); s.setStudentCode(sc.nextLine().trim());
        System.out.print("  First Name                     : "); s.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name                      : "); s.setLastName(sc.nextLine().trim());
        System.out.print("  Date of Birth (YYYY-MM-DD)     : "); s.setDateOfBirth(sc.nextLine().trim());
        System.out.print("  Gender (MALE/FEMALE/OTHER)     : "); s.setGender(sc.nextLine().trim().toUpperCase());
        System.out.print("  Phone                          : "); s.setPhone(sc.nextLine().trim());
        System.out.print("  Address                        : "); s.setAddress(sc.nextLine().trim());
        System.out.print("  City                           : "); s.setCity(sc.nextLine().trim());
        System.out.print("  State                          : "); s.setState(sc.nextLine().trim());
        System.out.print("  Pincode                        : "); s.setPincode(sc.nextLine().trim());
        System.out.print("  Department                     : "); s.setDepartment(sc.nextLine().trim());
        System.out.print("  Course                         : "); s.setCourse(sc.nextLine().trim());
        System.out.print("  Semester                       : "); s.setSemester(readInt());
        System.out.print("  Roll Number                    : "); s.setRollNumber(sc.nextLine().trim());
        System.out.print("  Year of Admission              : "); s.setYearOfAdmission(readInt());
        if (studentDAO.addStudent(s)) {
            ConsoleUtil.printSuccess("Student added! ID: " + s.getStudentId());
            userDAO.logAction(currentUser.getUserId(), "ADD_STUDENT", "Code=" + s.getStudentCode());
        } else {
            ConsoleUtil.printError("Failed. Username, email or roll number may already exist.");
        }
    }

    private static void viewAllStudents() {
        ConsoleUtil.printSection("ALL STUDENTS");
        List<Student> list = studentDAO.getAllStudents();
        if (list.isEmpty()) { System.out.println("  No students found."); return; }
        System.out.printf("  %-5s %-10s %-22s %-12s %-20s %-4s %-10s%n",
                "ID", "Code", "Name", "Roll No", "Department", "Sem", "Status");
        System.out.println("  " + "-".repeat(87));
        for (Student s : list)
            System.out.printf("  %-5d %-10s %-22s %-12s %-20s %-4d %-10s%n",
                    s.getStudentId(), s.getStudentCode(), s.getFullName(),
                    nvl(s.getRollNumber()), nvl(s.getDepartment()),
                    s.getSemester(), s.getStudentStatus());
    }

    private static void updateStudentStatus() {
        viewAllStudents();
        System.out.print("\n  Student ID  : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / SUSPENDED");
        System.out.print("  New Status  : "); String s = sc.nextLine().trim().toUpperCase();
        if (studentDAO.updateStudentStatus(id, s)) ConsoleUtil.printSuccess("Status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateSemester() {
        viewAllStudents();
        System.out.print("\n  Student ID  : "); int id = readInt();
        System.out.print("  New Semester: "); int sem = readInt();
        if (studentDAO.updateSemester(id, sem)) ConsoleUtil.printSuccess("Semester updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SUBJECT MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageSubjects() {
        ConsoleUtil.printSection("MANAGE SUBJECTS");
        System.out.println("  1. Add New Subject");
        System.out.println("  2. View All Subjects");
        System.out.println("  3. Update Subject Status");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addSubject();          break;
            case 2: viewAllSubjects();     break;
            case 3: updateSubjectStatus(); break;
        }
    }

    private static void addSubject() {
        ConsoleUtil.printSection("ADD NEW SUBJECT");
        Subject s = new Subject();
        System.out.print("  Subject Code (e.g. CS307)  : "); s.setSubjectCode(sc.nextLine().trim().toUpperCase());
        System.out.print("  Subject Name               : "); s.setSubjectName(sc.nextLine().trim());
        System.out.print("  Department                 : "); s.setDepartment(sc.nextLine().trim());
        System.out.print("  Credits                    : "); s.setCredits(readInt());
        System.out.println("  Type: THEORY / PRACTICAL / PROJECT");
        System.out.print("  Type                       : "); s.setSubjectType(sc.nextLine().trim().toUpperCase());
        if (examDAO.addSubject(s)) {
            ConsoleUtil.printSuccess("Subject added! ID: " + s.getSubjectId());
            userDAO.logAction(currentUser.getUserId(), "ADD_SUBJECT", "Code=" + s.getSubjectCode());
        } else {
            ConsoleUtil.printError("Failed. Subject code may already exist.");
        }
    }

    private static void viewAllSubjects() {
        ConsoleUtil.printSection("ALL SUBJECTS");
        List<Subject> list = examDAO.getAllSubjects();
        if (list.isEmpty()) { System.out.println("  No subjects found."); return; }
        System.out.printf("  %-5s %-8s %-35s %-20s %-4s %-12s %-8s%n",
                "ID", "Code", "Name", "Department", "Crd", "Type", "Status");
        System.out.println("  " + "-".repeat(96));
        for (Subject s : list)
            System.out.printf("  %-5d %-8s %-35s %-20s %-4d %-12s %-8s%n",
                    s.getSubjectId(), s.getSubjectCode(), s.getSubjectName(),
                    nvl(s.getDepartment()), s.getCredits(),
                    s.getSubjectType(), s.getStatus());
    }

    private static void updateSubjectStatus() {
        viewAllSubjects();
        System.out.print("\n  Subject ID  : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE");
        System.out.print("  New Status  : "); String s = sc.nextLine().trim().toUpperCase();
        if (examDAO.updateSubjectStatus(id, s)) ConsoleUtil.printSuccess("Status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXAM MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageExams() {
        ConsoleUtil.printSection("MANAGE EXAMS");
        System.out.println("  1. Create New Exam");
        System.out.println("  2. View All Exams");
        System.out.println("  3. Update Exam Status");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: createExam();       break;
            case 2: viewAllExams();     break;
            case 3: updateExamStatus(); break;
        }
    }

    private static void createExam() {
        ConsoleUtil.printSection("CREATE NEW EXAM");
        Exam e = new Exam();
        System.out.print("  Exam Code (e.g. EX-2026-S5)  : "); e.setExamCode(sc.nextLine().trim().toUpperCase());
        System.out.print("  Exam Name                    : "); e.setExamName(sc.nextLine().trim());
        System.out.println("  Type: SEMESTER / SUPPLEMENTARY / ENTRANCE / CERTIFICATION");
        System.out.print("  Exam Type                    : "); e.setExamType(sc.nextLine().trim().toUpperCase());
        System.out.print("  Academic Year (e.g. 2025-26) : "); e.setAcademicYear(sc.nextLine().trim());
        System.out.print("  Semester (0 = all)           : "); e.setSemester(readInt());
        System.out.print("  Department                   : "); e.setDepartment(sc.nextLine().trim());
        System.out.print("  Reg Start Date (YYYY-MM-DD)  : "); e.setRegStartDate(sc.nextLine().trim());
        System.out.print("  Reg End Date   (YYYY-MM-DD)  : "); e.setRegEndDate(sc.nextLine().trim());
        System.out.print("  Exam Start Date (YYYY-MM-DD) : "); e.setExamStartDate(sc.nextLine().trim());
        System.out.print("  Exam End Date   (YYYY-MM-DD) : "); e.setExamEndDate(sc.nextLine().trim());
        System.out.print("  Fee per Subject (Rs.)        : "); e.setFeePerSubject(readDouble());
        System.out.print("  Max Subjects per Student     : "); e.setMaxSubjects(readInt());
        if (examDAO.createExam(e, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Exam created (UPCOMING)! ID: " + e.getExamId()
                    + ". Update status to REGISTRATION_OPEN when ready.");
            userDAO.logAction(currentUser.getUserId(), "CREATE_EXAM", "Code=" + e.getExamCode());
        } else {
            ConsoleUtil.printError("Failed. Exam code may already exist.");
        }
    }

    private static void viewAllExams() {
        ConsoleUtil.printSection("ALL EXAMS");
        List<Exam> list = examDAO.getAllExams();
        if (list.isEmpty()) { System.out.println("  No exams found."); return; }
        printExamTable(list);
    }

    private static void printExamTable(List<Exam> list) {
        System.out.printf("  %-5s %-14s %-32s %-4s %-12s %-12s %-22s%n",
                "ID", "Code", "Name", "Sem", "Reg End", "Exam Start", "Status");
        System.out.println("  " + "-".repeat(105));
        for (Exam e : list)
            System.out.printf("  %-5d %-14s %-32s %-4d %-12s %-12s %-22s%n",
                    e.getExamId(), e.getExamCode(), e.getExamName(),
                    e.getSemester(), nvl(e.getRegEndDate()),
                    nvl(e.getExamStartDate()), e.getStatus());
    }

    private static void updateExamStatus() {
        viewAllExams();
        System.out.print("\n  Exam ID    : "); int id = readInt();
        System.out.println("  Status: UPCOMING / REGISTRATION_OPEN / REGISTRATION_CLOSED / ONGOING / COMPLETED / CANCELLED");
        System.out.print("  New Status : "); String s = sc.nextLine().trim().toUpperCase();
        if (examDAO.updateExamStatus(id, s)) {
            ConsoleUtil.printSuccess("Exam status updated to " + s);
        } else {
            ConsoleUtil.printError("Failed.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXAM SCHEDULE MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageExamSchedule() {
        ConsoleUtil.printSection("EXAM SCHEDULE MANAGEMENT");
        System.out.println("  1. Add Subject to Exam");
        System.out.println("  2. View Exam Schedule");
        System.out.println("  3. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addSubjectToExam(); break;
            case 2: viewExamSchedule(); break;
        }
    }

    private static void addSubjectToExam() {
        ConsoleUtil.printSection("ADD SUBJECT TO EXAM");
        viewAllExams();
        System.out.print("\n  Exam ID    : "); int examId = readInt();
        viewAllSubjects();
        System.out.print("\n  Subject ID : "); int subjectId = readInt();
        ExamSubject es = new ExamSubject();
        es.setExamId(examId);
        es.setSubjectId(subjectId);
        System.out.print("  Exam Date (YYYY-MM-DD)  : "); es.setExamDate(sc.nextLine().trim());
        System.out.print("  Exam Time (HH:MM:SS)    : "); es.setExamTime(sc.nextLine().trim());
        System.out.print("  Duration (minutes)      : "); es.setDurationMins(readInt());
        System.out.print("  Max Marks               : "); es.setMaxMarks(readInt());
        System.out.print("  Pass Marks              : "); es.setPassMarks(readInt());
        System.out.print("  Venue                   : "); es.setVenue(sc.nextLine().trim());
        if (examDAO.addExamSubject(es)) {
            ConsoleUtil.printSuccess("Subject added to exam schedule! ID: " + es.getId());
            userDAO.logAction(currentUser.getUserId(), "ADD_EXAM_SUBJECT",
                    "ExamID=" + examId + " SubjectID=" + subjectId);
        } else {
            ConsoleUtil.printError("Failed. Subject may already exist in this exam.");
        }
    }

    private static void viewExamSchedule() {
        viewAllExams();
        System.out.print("\n  Exam ID: "); int id = readInt();
        Exam exam = examDAO.getExamById(id);
        if (exam == null) { ConsoleUtil.printError("Exam not found."); return; }
        ConsoleUtil.printSection("SCHEDULE — " + exam.getExamName());
        List<ExamSubject> list = examDAO.getExamSchedule(id);
        if (list.isEmpty()) { System.out.println("  No subjects scheduled yet."); return; }
        System.out.printf("  %-5s %-8s %-32s %-12s %-8s %-5s %-5s %-20s%n",
                "ID", "Code", "Subject", "Date", "Time", "Max", "Pass", "Venue");
        System.out.println("  " + "-".repeat(100));
        for (ExamSubject es : list)
            System.out.printf("  %-5d %-8s %-32s %-12s %-8s %-5d %-5d %-20s%n",
                    es.getId(), es.getSubjectCode(), es.getSubjectName(),
                    nvl(es.getExamDate()), nvl(es.getExamTime()),
                    es.getMaxMarks(), es.getPassMarks(), nvl(es.getVenue()));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REGISTRATION MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void viewAllRegistrations() {
        ConsoleUtil.printSection("ALL REGISTRATIONS");
        List<Registration> list = registrationDAO.getAllRegistrations();
        if (list.isEmpty()) { System.out.println("  No registrations found."); return; }
        printRegistrationTable(list);
    }

    private static void approveRegistration() {
        ConsoleUtil.printSection("APPROVE / CANCEL REGISTRATION");
        viewAllRegistrations();
        System.out.print("\n  Registration ID : "); int id = readInt();
        System.out.println("  Action: CONFIRMED / CANCELLED");
        System.out.print("  New Status      : "); String s = sc.nextLine().trim().toUpperCase();
        if (registrationDAO.updateRegistrationStatus(id, s)) {
            ConsoleUtil.printSuccess("Registration status updated to " + s);
            userDAO.logAction(currentUser.getUserId(), "UPDATE_REG",
                    "RegID=" + id + " Status=" + s);
        } else {
            ConsoleUtil.printError("Failed.");
        }
    }

    private static void printRegistrationTable(List<Registration> list) {
        if (list.isEmpty()) { System.out.println("  No registrations found."); return; }
        System.out.printf("  %-5s %-15s %-22s %-12s %-28s %-4s %-8s %-8s %-10s%n",
                "ID", "Reg No", "Student", "Roll No", "Exam", "Sub", "Fee", "Paid", "Status");
        System.out.println("  " + "-".repeat(115));
        for (Registration r : list) {
            String examShort = r.getExamName() != null && r.getExamName().length() > 26
                    ? r.getExamName().substring(0, 25) + "." : nvl(r.getExamName());
            System.out.printf("  %-5d %-15s %-22s %-12s %-28s %-4d %-8.0f %-8s %-10s%n",
                    r.getRegistrationId(), r.getRegNumber(), r.getStudentName(),
                    nvl(r.getRollNumber()), examShort, r.getTotalSubjects(),
                    r.getTotalFee(), r.isFeePaid() ? "YES" : "NO", r.getStatus());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HALL TICKET
    // ══════════════════════════════════════════════════════════════════════════
    private static void issueHallTicketsMenu() {
        ConsoleUtil.printSection("ISSUE HALL TICKETS");
        System.out.println("  1. Issue for specific registration");
        System.out.println("  2. Bulk issue for an exam (all confirmed + fee paid)");
        System.out.println("  3. Print hall ticket");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            viewAllRegistrations();
            System.out.print("\n  Registration ID: "); int id = readInt();
            if (registrationDAO.issueHallTicket(id)) {
                Registration r = registrationDAO.getRegistrationById(id);
                ConsoleUtil.printSuccess("Hall ticket issued: " + r.getHallTicketNo());
                userDAO.logAction(currentUser.getUserId(), "ISSUE_HALL_TICKET", "RegID=" + id);
            } else {
                ConsoleUtil.printError("Failed. Ensure fee is paid and ticket not already issued.");
            }
        } else if (ch == 2) {
            viewAllExams();
            System.out.print("\n  Exam ID: "); int examId = readInt();
            int count = registrationDAO.issueHallTicketsBulk(examId);
            ConsoleUtil.printSuccess("Hall tickets issued for " + count + " student(s).");
            userDAO.logAction(currentUser.getUserId(), "BULK_HALL_TICKET",
                    "ExamID=" + examId + " Count=" + count);
        } else if (ch == 3) {
            viewAllRegistrations();
            System.out.print("\n  Registration ID: "); int id = readInt();
            registrationDAO.printHallTicket(id, examDAO, studentDAO);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RESULT ENTRY
    // ══════════════════════════════════════════════════════════════════════════
    private static void enterResultsMenu() {
        ConsoleUtil.printSection("ENTER RESULTS");
        viewAllExams();
        System.out.print("\n  Exam ID: "); int examId = readInt();
        Exam exam = examDAO.getExamById(examId);
        if (exam == null) { ConsoleUtil.printError("Exam not found."); return; }

        List<Registration> regs = registrationDAO.getRegistrationsByExam(examId);
        if (regs.isEmpty()) {
            System.out.println("  No confirmed registrations for this exam."); return;
        }
        System.out.println("  Registrations for: " + exam.getExamName());
        printRegistrationTable(regs);
        System.out.print("\n  Registration ID to enter marks: "); int regId = readInt();
        Registration reg = registrationDAO.getRegistrationById(regId);
        if (reg == null) { ConsoleUtil.printError("Registration not found."); return; }

        ConsoleUtil.printSection("ENTER MARKS — " + reg.getStudentName());
        List<ExamSubject> subjects = registrationDAO.getSubjectsForRegistration(regId, examDAO);
        if (subjects.isEmpty()) {
            System.out.println("  No subjects found for this registration."); return;
        }
        for (ExamSubject es : subjects) {
            System.out.printf("  %s — %s (Max: %d, Pass: %d)%n",
                    es.getSubjectCode(), es.getSubjectName(),
                    es.getMaxMarks(), es.getPassMarks());
            System.out.print("  Marks (-1 = ABSENT): "); double marks = readDouble();
            boolean ok;
            if (marks < 0) {
                ok = resultDAO.markAbsent(regId, reg.getStudentId(), examId,
                        es.getSubjectId(), es.getMaxMarks(), es.getPassMarks(),
                        currentUser.getUserId());
                if (ok) ConsoleUtil.printInfo("Marked ABSENT.");
            } else {
                ok = resultDAO.enterResult(regId, reg.getStudentId(), examId,
                        es.getSubjectId(), marks, es.getMaxMarks(), es.getPassMarks(),
                        currentUser.getUserId());
                if (ok) ConsoleUtil.printSuccess("Marks saved.");
            }
            if (!ok) ConsoleUtil.printError("Failed to save marks for " + es.getSubjectCode());
        }
        ConsoleUtil.printSuccess("All marks entered for " + reg.getStudentName());
        userDAO.logAction(currentUser.getUserId(), "ENTER_RESULTS",
                "RegID=" + regId + " ExamID=" + examId);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PUBLISH RESULTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void publishResultsMenu() {
        ConsoleUtil.printSection("PUBLISH RESULTS");
        viewAllExams();
        System.out.print("\n  Exam ID to publish: "); int examId = readInt();
        Exam exam = examDAO.getExamById(examId);
        if (exam == null) { ConsoleUtil.printError("Exam not found."); return; }
        System.out.print("  Publish all results for '" + exam.getExamName() + "'? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) return;
        int count = resultDAO.publishResults(examId);
        if (count > 0) {
            ConsoleUtil.printSuccess(count + " result(s) published for " + exam.getExamName());
            userDAO.logAction(currentUser.getUserId(), "PUBLISH_RESULTS",
                    "ExamID=" + examId + " Count=" + count);
        } else {
            ConsoleUtil.printError("No unpublished results found for this exam.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  VIEW RESULTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void viewResultsMenu() {
        ConsoleUtil.printSection("VIEW RESULTS");
        System.out.println("  1. View results by exam");
        System.out.println("  2. Exam result summary (pass/fail counts)");
        System.out.println("  3. Print marksheet for a registration");
        System.out.println("  4. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: viewResultsByExam();   break;
            case 2: examResultSummary();   break;
            case 3: printMarksheetAdmin(); break;
        }
    }

    private static void viewResultsByExam() {
        viewAllExams();
        System.out.print("\n  Exam ID: "); int examId = readInt();
        ConsoleUtil.printSection("RESULTS — Exam " + examId);
        List<Result> list = resultDAO.getResultsByExam(examId);
        if (list.isEmpty()) { System.out.println("  No published results found."); return; }
        System.out.printf("  %-12s %-22s %-8s %-30s %-6s %-6s %-6s %-8s%n",
                "Roll No", "Student", "Code", "Subject", "Max", "Got", "Grade", "Status");
        System.out.println("  " + "-".repeat(100));
        for (Result r : list) {
            String subj = r.getSubjectName().length() > 28
                    ? r.getSubjectName().substring(0, 27) + "." : r.getSubjectName();
            System.out.printf("  %-12s %-22s %-8s %-30s %-6d %-6.1f %-6s %-8s%n",
                    nvl(r.getRollNumber()), r.getStudentName(),
                    r.getSubjectCode(), subj, r.getMaxMarks(),
                    r.getMarksObtained(), r.getGrade(), r.getResultStatus());
        }
    }

    private static void examResultSummary() {
        viewAllExams();
        System.out.print("\n  Exam ID: "); int examId = readInt();
        Exam exam = examDAO.getExamById(examId);
        if (exam == null) { ConsoleUtil.printError("Exam not found."); return; }
        ConsoleUtil.printSection("RESULT SUMMARY — " + exam.getExamName());
        resultDAO.printExamResultSummary(examId);
    }

    private static void printMarksheetAdmin() {
        viewAllRegistrations();
        System.out.print("\n  Registration ID: "); int id = readInt();
        resultDAO.printMarksheet(id);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void reportsMenu() {
        ConsoleUtil.printSection("REPORTS");
        System.out.println("  1. All Students");
        System.out.println("  2. All Exams");
        System.out.println("  3. All Registrations");
        System.out.println("  4. Registrations by Exam");
        System.out.println("  5. Exam Result Summary");
        System.out.println("  6. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: viewAllStudents();      break;
            case 2: viewAllExams();         break;
            case 3: viewAllRegistrations(); break;
            case 4:
                viewAllExams();
                System.out.print("\n  Exam ID: "); int eid = readInt();
                ConsoleUtil.printSection("REGISTRATIONS FOR EXAM " + eid);
                printRegistrationTable(registrationDAO.getRegistrationsByExam(eid));
                break;
            case 5: examResultSummary(); break;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  USER MANAGEMENT (Admin only)
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageUsers() {
        ConsoleUtil.printSection("MANAGE USERS");
        List<User> list = userDAO.getAllUsers();
        System.out.printf("  %-5s %-20s %-30s %-12s %-8s%n",
                "ID", "Username", "Email", "Role", "Active");
        System.out.println("  " + "-".repeat(77));
        for (User u : list)
            System.out.printf("  %-5d %-20s %-30s %-12s %-8s%n",
                    u.getUserId(), u.getUsername(), u.getEmail(),
                    u.getRole(), u.isStatus() ? "Yes" : "No");
        System.out.println("\n  1. Add User   2. Deactivate User   3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            System.out.print("  Username : "); String un = sc.nextLine().trim();
            System.out.print("  Password : "); String pw = sc.nextLine().trim();
            System.out.print("  Email    : "); String em = sc.nextLine().trim();
            System.out.println("  Role: ADMIN / EXAMINER / STUDENT");
            System.out.print("  Role     : "); String rl = sc.nextLine().trim().toUpperCase();
            if (userDAO.createUser(un, pw, em, rl)) ConsoleUtil.printSuccess("User created.");
            else ConsoleUtil.printError("Failed. Username or email may already exist.");
        } else if (ch == 2) {
            System.out.print("  User ID to deactivate: "); int id = readInt();
            if (userDAO.deactivateUser(id)) ConsoleUtil.printSuccess("User deactivated.");
            else ConsoleUtil.printError("Failed.");
        }
    }

    private static void viewSystemLogs() {
        ConsoleUtil.printSection("SYSTEM LOGS (Last 30)");
        userDAO.printSystemLogs();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private static void logout() {
        userDAO.logAction(currentUser.getUserId(), "LOGOUT", "User logged out");
        ConsoleUtil.printSuccess("Logged out. Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    private static int readInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private static double readDouble() {
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (Exception e) { return 0.0; }
    }

    private static String nvl(String s) { return s == null ? "-" : s; }
}
