package com.spms.model;

public class LeaveApplication {
    private int     leaveId;
    private int     employeeId;
    private String  employeeName;
    private int     leaveTypeId;
    private String  leaveTypeName;
    private String  startDate;
    private String  endDate;
    private int     totalDays;
    private String  reason;
    private String  status;
    private String  appliedOn;
    private Integer approvedBy;
    private String  approvedByName;
    private String  approvedOn;
    private String  comments;

    public LeaveApplication() {}

    public LeaveApplication(int leaveId, int employeeId, int leaveTypeId,
                             String startDate, String endDate, String reason) {
        this.leaveId      = leaveId;
        this.employeeId   = employeeId;
        this.leaveTypeId  = leaveTypeId;
        this.startDate    = startDate;
        this.endDate      = endDate;
        this.reason       = reason;
        this.status       = "PENDING";
    }

    public int     getLeaveId()               { return leaveId; }
    public void    setLeaveId(int v)          { this.leaveId = v; }
    public int     getEmployeeId()            { return employeeId; }
    public void    setEmployeeId(int v)       { this.employeeId = v; }
    public String  getEmployeeName()          { return employeeName; }
    public void    setEmployeeName(String v)  { this.employeeName = v; }
    public int     getLeaveTypeId()           { return leaveTypeId; }
    public void    setLeaveTypeId(int v)      { this.leaveTypeId = v; }
    public String  getLeaveTypeName()         { return leaveTypeName; }
    public void    setLeaveTypeName(String v) { this.leaveTypeName = v; }
    public String  getStartDate()             { return startDate; }
    public void    setStartDate(String v)     { this.startDate = v; }
    public String  getEndDate()               { return endDate; }
    public void    setEndDate(String v)       { this.endDate = v; }
    public int     getTotalDays()             { return totalDays; }
    public void    setTotalDays(int v)        { this.totalDays = v; }
    public String  getReason()                { return reason; }
    public void    setReason(String v)        { this.reason = v; }
    public String  getStatus()                { return status; }
    public void    setStatus(String v)        { this.status = v; }
    public String  getAppliedOn()             { return appliedOn; }
    public void    setAppliedOn(String v)     { this.appliedOn = v; }
    public Integer getApprovedBy()            { return approvedBy; }
    public void    setApprovedBy(Integer v)   { this.approvedBy = v; }
    public String  getApprovedByName()        { return approvedByName; }
    public void    setApprovedByName(String v){ this.approvedByName = v; }
    public String  getApprovedOn()            { return approvedOn; }
    public void    setApprovedOn(String v)    { this.approvedOn = v; }
    public String  getComments()              { return comments; }
    public void    setComments(String v)      { this.comments = v; }
}
