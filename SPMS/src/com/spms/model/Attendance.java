package com.spms.model;

public class Attendance {
    private int    attendanceId;
    private int    employeeId;
    private String employeeName;
    private String attendanceDate;
    private String checkInTime;
    private String checkOutTime;
    private double totalHours;
    private String status;
    private String remarks;

    public Attendance() {}

    public int    getAttendanceId()           { return attendanceId; }
    public void   setAttendanceId(int v)      { this.attendanceId = v; }
    public int    getEmployeeId()             { return employeeId; }
    public void   setEmployeeId(int v)        { this.employeeId = v; }
    public String getEmployeeName()           { return employeeName; }
    public void   setEmployeeName(String v)   { this.employeeName = v; }
    public String getAttendanceDate()         { return attendanceDate; }
    public void   setAttendanceDate(String v) { this.attendanceDate = v; }
    public String getCheckInTime()            { return checkInTime; }
    public void   setCheckInTime(String v)    { this.checkInTime = v; }
    public String getCheckOutTime()           { return checkOutTime; }
    public void   setCheckOutTime(String v)   { this.checkOutTime = v; }
    public double getTotalHours()             { return totalHours; }
    public void   setTotalHours(double v)     { this.totalHours = v; }
    public String getStatus()                 { return status; }
    public void   setStatus(String v)         { this.status = v; }
    public String getRemarks()                { return remarks; }
    public void   setRemarks(String v)        { this.remarks = v; }
}
