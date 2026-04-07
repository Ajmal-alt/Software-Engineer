package com.spms.model;

public class Payroll {
    private int    payrollId;
    private int    employeeId;
    private String employeeName;
    private int    month;
    private int    year;
    private int    workingDays;
    private int    presentDays;
    private double grossSalary;
    private double totalDeductions;
    private double netSalary;
    private String paymentDate;
    private String status;
    private int    processedBy;

    public int    getPayrollId()             { return payrollId; }
    public void   setPayrollId(int v)        { this.payrollId = v; }
    public int    getEmployeeId()            { return employeeId; }
    public void   setEmployeeId(int v)       { this.employeeId = v; }
    public String getEmployeeName()          { return employeeName; }
    public void   setEmployeeName(String v)  { this.employeeName = v; }
    public int    getMonth()                 { return month; }
    public void   setMonth(int v)            { this.month = v; }
    public int    getYear()                  { return year; }
    public void   setYear(int v)             { this.year = v; }
    public int    getWorkingDays()           { return workingDays; }
    public void   setWorkingDays(int v)      { this.workingDays = v; }
    public int    getPresentDays()           { return presentDays; }
    public void   setPresentDays(int v)      { this.presentDays = v; }
    public double getGrossSalary()           { return grossSalary; }
    public void   setGrossSalary(double v)   { this.grossSalary = v; }
    public double getTotalDeductions()       { return totalDeductions; }
    public void   setTotalDeductions(double v){ this.totalDeductions = v; }
    public double getNetSalary()             { return netSalary; }
    public void   setNetSalary(double v)     { this.netSalary = v; }
    public String getPaymentDate()           { return paymentDate; }
    public void   setPaymentDate(String v)   { this.paymentDate = v; }
    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status = v; }
    public int    getProcessedBy()           { return processedBy; }
    public void   setProcessedBy(int v)      { this.processedBy = v; }
}
