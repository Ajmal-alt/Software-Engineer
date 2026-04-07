package com.rtms.model;

public class Staff {
    private int    staffId;
    private String staffCode;
    private String firstName;
    private String lastName;
    private String roleTitle;
    private String department;
    private String phone;
    private String email;
    private String nationality;
    private String contractStart;
    private String contractEnd;
    private double salary;
    private String status;

    public String getFullName() { return firstName + " " + lastName; }

    public int    getStaffId()             { return staffId; }
    public void   setStaffId(int v)        { this.staffId=v; }
    public String getStaffCode()           { return staffCode; }
    public void   setStaffCode(String v)   { this.staffCode=v; }
    public String getFirstName()           { return firstName; }
    public void   setFirstName(String v)   { this.firstName=v; }
    public String getLastName()            { return lastName; }
    public void   setLastName(String v)    { this.lastName=v; }
    public String getRoleTitle()           { return roleTitle; }
    public void   setRoleTitle(String v)   { this.roleTitle=v; }
    public String getDepartment()          { return department; }
    public void   setDepartment(String v)  { this.department=v; }
    public String getPhone()               { return phone; }
    public void   setPhone(String v)       { this.phone=v; }
    public String getEmail()               { return email; }
    public void   setEmail(String v)       { this.email=v; }
    public String getNationality()         { return nationality; }
    public void   setNationality(String v) { this.nationality=v; }
    public String getContractStart()       { return contractStart; }
    public void   setContractStart(String v){ this.contractStart=v; }
    public String getContractEnd()         { return contractEnd; }
    public void   setContractEnd(String v) { this.contractEnd=v; }
    public double getSalary()              { return salary; }
    public void   setSalary(double v)      { this.salary=v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status=v; }
}
