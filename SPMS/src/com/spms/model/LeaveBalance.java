package com.spms.model;

public class LeaveBalance {
    private int    balanceId;
    private int    employeeId;
    private int    leaveTypeId;
    private String leaveTypeName;
    private int    year;
    private int    totalLeaves;
    private int    usedLeaves;
    private int    remainingLeaves;

    public int    getBalanceId()              { return balanceId; }
    public void   setBalanceId(int v)         { this.balanceId = v; }
    public int    getEmployeeId()             { return employeeId; }
    public void   setEmployeeId(int v)        { this.employeeId = v; }
    public int    getLeaveTypeId()            { return leaveTypeId; }
    public void   setLeaveTypeId(int v)       { this.leaveTypeId = v; }
    public String getLeaveTypeName()          { return leaveTypeName; }
    public void   setLeaveTypeName(String v)  { this.leaveTypeName = v; }
    public int    getYear()                   { return year; }
    public void   setYear(int v)              { this.year = v; }
    public int    getTotalLeaves()            { return totalLeaves; }
    public void   setTotalLeaves(int v)       { this.totalLeaves = v; }
    public int    getUsedLeaves()             { return usedLeaves; }
    public void   setUsedLeaves(int v)        { this.usedLeaves = v; }
    public int    getRemainingLeaves()        { return remainingLeaves; }
    public void   setRemainingLeaves(int v)   { this.remainingLeaves = v; }
}
