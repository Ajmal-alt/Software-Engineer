package com.spms.model;

public class Employee extends User {
    private int    employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String dateOfJoining;
    private String designation;
    private int    departmentId;
    private String departmentName;
    private String qualification;
    private String bankAccountNo;
    private String ifscCode;
    private String panCard;
    private String uanNumber;
    private String empStatus;

    public Employee() { super(); }

    public Employee(int userId, String username, String email, String role,
                    int employeeId, String employeeCode,
                    String firstName, String lastName, String designation) {
        super(userId, username, email, role);
        this.employeeId   = employeeId;
        this.employeeCode = employeeCode;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.designation  = designation;
    }

    @Override
    public void displayDashboard() {
        System.out.println("  Welcome, " + firstName + " " + lastName
                + " [" + employeeCode + "] – " + designation);
        if ("PROJECT_MANAGER".equals(getRole())) {
            System.out.println("  1. View My Profile");
            System.out.println("  2. Mark Attendance (Check-In / Check-Out)");
            System.out.println("  3. Apply for Leave");
            System.out.println("  4. View Leave Balance");
            System.out.println("  5. View My Payslip");
            System.out.println("  6. View Team Attendance");
            System.out.println("  7. Manage Pending Leaves");
            System.out.println("  8. Manage Projects");
            System.out.println("  9. Evaluate Performance");
            System.out.println("  10. Logout");
        } else {
            System.out.println("  1. View My Profile");
            System.out.println("  2. Mark Attendance (Check-In / Check-Out)");
            System.out.println("  3. Apply for Leave");
            System.out.println("  4. View Leave Balance");
            System.out.println("  5. View My Payslip");
            System.out.println("  6. View My Projects");
            System.out.println("  7. View My Performance");
            System.out.println("  8. Logout");
        }
    }

    public String getFullName()                        { return firstName + " " + lastName; }

    // Getters & Setters
    public int    getEmployeeId()                      { return employeeId; }
    public void   setEmployeeId(int v)                 { this.employeeId = v; }
    public String getEmployeeCode()                    { return employeeCode; }
    public void   setEmployeeCode(String v)            { this.employeeCode = v; }
    public String getFirstName()                       { return firstName; }
    public void   setFirstName(String v)               { this.firstName = v; }
    public String getLastName()                        { return lastName; }
    public void   setLastName(String v)                { this.lastName = v; }
    public String getDateOfBirth()                     { return dateOfBirth; }
    public void   setDateOfBirth(String v)             { this.dateOfBirth = v; }
    public String getGender()                          { return gender; }
    public void   setGender(String v)                  { this.gender = v; }
    public String getPhone()                           { return phone; }
    public void   setPhone(String v)                   { this.phone = v; }
    public String getAddress()                         { return address; }
    public void   setAddress(String v)                 { this.address = v; }
    public String getCity()                            { return city; }
    public void   setCity(String v)                    { this.city = v; }
    public String getState()                           { return state; }
    public void   setState(String v)                   { this.state = v; }
    public String getPincode()                         { return pincode; }
    public void   setPincode(String v)                 { this.pincode = v; }
    public String getDateOfJoining()                   { return dateOfJoining; }
    public void   setDateOfJoining(String v)           { this.dateOfJoining = v; }
    public String getDesignation()                     { return designation; }
    public void   setDesignation(String v)             { this.designation = v; }
    public int    getDepartmentId()                    { return departmentId; }
    public void   setDepartmentId(int v)               { this.departmentId = v; }
    public String getDepartmentName()                  { return departmentName; }
    public void   setDepartmentName(String v)          { this.departmentName = v; }
    public String getQualification()                   { return qualification; }
    public void   setQualification(String v)           { this.qualification = v; }
    public String getBankAccountNo()                   { return bankAccountNo; }
    public void   setBankAccountNo(String v)           { this.bankAccountNo = v; }
    public String getIfscCode()                        { return ifscCode; }
    public void   setIfscCode(String v)                { this.ifscCode = v; }
    public String getPanCard()                         { return panCard; }
    public void   setPanCard(String v)                 { this.panCard = v; }
    public String getUanNumber()                       { return uanNumber; }
    public void   setUanNumber(String v)               { this.uanNumber = v; }
    public String getEmpStatus()                       { return empStatus; }
    public void   setEmpStatus(String v)               { this.empStatus = v; }
}
