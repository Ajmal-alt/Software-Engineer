package com.ers.model;

public class Registration {
    private int     registrationId;
    private String  regNumber;
    private int     studentId;
    private String  studentName;
    private String  rollNumber;
    private int     examId;
    private String  examName;
    private String  registeredOn;
    private int     totalSubjects;
    private double  totalFee;
    private boolean feePaid;
    private String  paymentRef;
    private String  paymentDate;
    private String  hallTicketNo;
    private boolean hallTicketIssued;
    private String  status;
    private String  remarks;

    public int     getRegistrationId()           { return registrationId; }
    public void    setRegistrationId(int v)      { this.registrationId = v; }
    public String  getRegNumber()                { return regNumber; }
    public void    setRegNumber(String v)        { this.regNumber = v; }
    public int     getStudentId()                { return studentId; }
    public void    setStudentId(int v)           { this.studentId = v; }
    public String  getStudentName()              { return studentName; }
    public void    setStudentName(String v)      { this.studentName = v; }
    public String  getRollNumber()               { return rollNumber; }
    public void    setRollNumber(String v)       { this.rollNumber = v; }
    public int     getExamId()                   { return examId; }
    public void    setExamId(int v)              { this.examId = v; }
    public String  getExamName()                 { return examName; }
    public void    setExamName(String v)         { this.examName = v; }
    public String  getRegisteredOn()             { return registeredOn; }
    public void    setRegisteredOn(String v)     { this.registeredOn = v; }
    public int     getTotalSubjects()            { return totalSubjects; }
    public void    setTotalSubjects(int v)       { this.totalSubjects = v; }
    public double  getTotalFee()                 { return totalFee; }
    public void    setTotalFee(double v)         { this.totalFee = v; }
    public boolean isFeePaid()                   { return feePaid; }
    public void    setFeePaid(boolean v)         { this.feePaid = v; }
    public String  getPaymentRef()               { return paymentRef; }
    public void    setPaymentRef(String v)       { this.paymentRef = v; }
    public String  getPaymentDate()              { return paymentDate; }
    public void    setPaymentDate(String v)      { this.paymentDate = v; }
    public String  getHallTicketNo()             { return hallTicketNo; }
    public void    setHallTicketNo(String v)     { this.hallTicketNo = v; }
    public boolean isHallTicketIssued()          { return hallTicketIssued; }
    public void    setHallTicketIssued(boolean v){ this.hallTicketIssued = v; }
    public String  getStatus()                   { return status; }
    public void    setStatus(String v)           { this.status = v; }
    public String  getRemarks()                  { return remarks; }
    public void    setRemarks(String v)          { this.remarks = v; }
}
