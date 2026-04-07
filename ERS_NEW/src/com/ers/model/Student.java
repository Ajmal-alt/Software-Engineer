package com.ers.model;

public class Student extends User {
    private int    studentId;
    private String studentCode;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String department;
    private String course;
    private int    semester;
    private String rollNumber;
    private int    yearOfAdmission;
    private String studentStatus;

    public Student() { super(); }

    public Student(int userId, String username, String email, String role,
                   int studentId, String studentCode, String firstName, String lastName) {
        super(userId, username, email, role);
        this.studentId   = studentId;
        this.studentCode = studentCode;
        this.firstName   = firstName;
        this.lastName    = lastName;
    }

    @Override
    public void displayDashboard() {
        System.out.println("  Welcome, " + firstName + " " + lastName
                + " [" + studentCode + " | Sem " + semester + "]");
        System.out.println("  1.  View My Profile");
        System.out.println("  2.  View Available Exams");
        System.out.println("  3.  Register for Exam");
        System.out.println("  4.  View My Registrations");
        System.out.println("  5.  Pay Exam Fee");
        System.out.println("  6.  Download Hall Ticket");
        System.out.println("  7.  View My Results");
        System.out.println("  8.  View Result Summary");
        System.out.println("  9.  Logout");
    }

    public String getFullName() { return firstName + " " + lastName; }

    public int    getStudentId()             { return studentId; }
    public void   setStudentId(int v)        { this.studentId = v; }
    public String getStudentCode()           { return studentCode; }
    public void   setStudentCode(String v)   { this.studentCode = v; }
    public String getFirstName()             { return firstName; }
    public void   setFirstName(String v)     { this.firstName = v; }
    public String getLastName()              { return lastName; }
    public void   setLastName(String v)      { this.lastName = v; }
    public String getDateOfBirth()           { return dateOfBirth; }
    public void   setDateOfBirth(String v)   { this.dateOfBirth = v; }
    public String getGender()                { return gender; }
    public void   setGender(String v)        { this.gender = v; }
    public String getPhone()                 { return phone; }
    public void   setPhone(String v)         { this.phone = v; }
    public String getAddress()               { return address; }
    public void   setAddress(String v)       { this.address = v; }
    public String getCity()                  { return city; }
    public void   setCity(String v)          { this.city = v; }
    public String getState()                 { return state; }
    public void   setState(String v)         { this.state = v; }
    public String getPincode()               { return pincode; }
    public void   setPincode(String v)       { this.pincode = v; }
    public String getDepartment()            { return department; }
    public void   setDepartment(String v)    { this.department = v; }
    public String getCourse()                { return course; }
    public void   setCourse(String v)        { this.course = v; }
    public int    getSemester()              { return semester; }
    public void   setSemester(int v)         { this.semester = v; }
    public String getRollNumber()            { return rollNumber; }
    public void   setRollNumber(String v)    { this.rollNumber = v; }
    public int    getYearOfAdmission()       { return yearOfAdmission; }
    public void   setYearOfAdmission(int v)  { this.yearOfAdmission = v; }
    public String getStudentStatus()         { return studentStatus; }
    public void   setStudentStatus(String v) { this.studentStatus = v; }
}
